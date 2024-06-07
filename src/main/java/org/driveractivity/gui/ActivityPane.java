package org.driveractivity.gui;

import javafx.collections.ObservableList;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import lombok.Getter;
import lombok.Setter;
import org.driveractivity.entity.Activity;
import org.driveractivity.service.DriverInterface;
import org.driveractivity.service.DriverServiceListener;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class ActivityPane extends FlowPane implements ActivityDisplay, DriverServiceListener {
    private DriverInterface driverData;
    @Setter @Getter
    private MainController mainController;

    public ActivityPane() {
        this.setRowValignment(VPos.BOTTOM);
    }

    @Override
    public void load(DriverInterface driverData) {
        this.driverData = driverData;
        driverData.addDriverServiceListener(this);
        reload(driverData.getBlocks());
    }

    @Override
    public void reload(List<Activity> newActivities) {
        ObservableList<Node> children = this.getChildren();
        // temporary list to only notify the children listener once
        List<Node> newChildren = new ArrayList<>();
        ListIterator<Activity> iterator = newActivities.listIterator();
        while (iterator.hasNext()) {
            int index = iterator.nextIndex();
            Activity activity = iterator.next();
            newChildren.add(new ActivityBlock(this, activity, index));
        }
        // temporarily disable managed to improve performance of mass adding and only do one layout pass
        this.setManaged(false);
        try {
            children.clear();
            children.addAll(newChildren);
        } finally {
            this.setManaged(true);
        }
    }

    @Override
    public DriverInterface getDriverInterface() {
        return driverData;
    }

    private void updateIndices() {
        int index = 0;
        for (Node child : getChildren()) {
            if(child instanceof ActivityBlock activityBlock) {
                activityBlock.setActivityIndex(index++);
            }
        }
    }

    @Override
    public void onAllActivitiesUpdated(List<Activity> activities) {
        reload(activities);
    }

    @Override
    public void onActivityRemoved(int index) {
        getChildren().remove(index);
        updateIndices();
    }

    @Override
    public void onActivityAdded(int index, Activity activity) {
        getChildren().add(index, new ActivityBlock(this, activity, index));
        updateIndices();
    }

    @Override
    public void onActivityUpdated(int index) {
        if(getChildren().get(index) instanceof ActivityBlock activityBlock) {
            activityBlock.update();
        }
    }

    @Override
    public void onActivitiesMerged(int index) {
        // TODO highlight
    }
}
