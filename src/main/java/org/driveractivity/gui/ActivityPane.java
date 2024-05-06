package org.driveractivity.gui;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import org.driveractivity.entity.Activity;

import java.util.List;

public class ActivityPane extends FlowPane implements ActivityDisplay {
    @Override
    public void load(List<Activity> activities) {
        ObservableList<Node> children = this.getChildren();
        activities.stream().map(ActivityBlock::new).forEach(children::add);
    }
}
