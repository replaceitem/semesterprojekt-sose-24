package org.driveractivity.gui;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import org.driveractivity.entity.Activity;

import java.util.List;
import java.util.ListIterator;

public class ActivityPane extends FlowPane implements ActivityDisplay {
    @Override
    public void load(List<Activity> activities) {
        ObservableList<Node> children = this.getChildren();
        children.clear();
        ListIterator<Activity> iterator = activities.listIterator();
        while (iterator.hasNext()) {
            int activityIndex = iterator.nextIndex();
            Activity activity = iterator.next();
            children.add(new ActivityBlock(activity, activityIndex));
        }
    }
    public void addBack(Activity activity) {
        ObservableList<Node> children = this.getChildren();
        int newIndex = children.reversed().stream()
                .filter(node -> node instanceof ActivityBlock)
                .findFirst()
                .map(node -> ((ActivityBlock) node).getActivityIndex())
                .orElse(0) + 1;
        children.add(new ActivityBlock(activity, newIndex));
    }
}
