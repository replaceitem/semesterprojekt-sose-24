package org.driveractivity.gui;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import org.driveractivity.entity.Activity;
import org.driveractivity.service.DriverInterface;

import java.time.LocalDateTime;
import java.util.ListIterator;

public class ActivityPane extends FlowPane implements ActivityDisplay {
    private DriverInterface driverData;
    
    @Override
    public void load(DriverInterface driverData) {
        this.driverData = driverData;
        ObservableList<Node> children = this.getChildren();
        children.clear();
        ListIterator<Activity> iterator = driverData.getBlocks().listIterator();
        while (iterator.hasNext()) {
            int activityIndex = iterator.nextIndex();
            Activity activity = iterator.next();
            children.add(new ActivityBlock(this, activity, activityIndex));
        }
    }
    
    @Override
    public void reload() {
        ListIterator<Node> iterator = this.getChildren().listIterator();
        while (iterator.hasNext()) {
            int index = iterator.nextIndex();
            Node node = iterator.next();
            if(node instanceof ActivityBlock activityBlock) {
                activityBlock.reload();
                activityBlock.setActivityIndex(index);
            }
        }
    }
    
    @Override
    public void addBack(Activity activity) {
        this.addActivity(this.getChildren().size(), activity);
    }
    
    @Override
    public void addActivity(int index, Activity activity) {
        driverData.addBlock(index, activity);
        this.getChildren().add(index, new ActivityBlock(this, activity, index));
        reload();
    }
    
    @Override
    public void removeActivity(int index) {
        driverData.removeBlock(index);
        this.getChildren().remove(index);
        reload();
    }
    
    @Override
    public DriverInterface getDriverInterface() {
        return driverData;
    }
}
