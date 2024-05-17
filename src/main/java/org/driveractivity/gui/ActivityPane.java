package org.driveractivity.gui;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import org.driveractivity.entity.Activity;
import org.driveractivity.service.DriverInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class ActivityPane extends FlowPane implements ActivityDisplay {
    private DriverInterface driverData;
    
    @Override
    public void load(DriverInterface driverData) {
        this.driverData = driverData;
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
    public void addBack(Activity activity) {
        this.addActivity(this.getChildren().size(), activity);
    }
    
    @Override
    public void addActivity(int index, Activity activity) {
        ArrayList<Activity> newActivities = driverData.addBlock(index, activity);
        reload(newActivities);
    }
    
    @Override
    public void removeActivity(int index) {
        
        ArrayList<Activity> newActivities = driverData.removeBlock(index);
        reload(newActivities);
    }
    
    @Override
    public DriverInterface getDriverInterface() {
        return driverData;
    }
}
