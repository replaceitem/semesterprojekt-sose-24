package org.driveractivity.gui;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.input.*;
import javafx.scene.layout.FlowPane;
import lombok.Getter;
import lombok.Setter;
import org.driveractivity.entity.Activity;
import org.driveractivity.service.DriverInterface;
import org.driveractivity.service.DriverServiceListener;
import org.jetbrains.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

public class ActivityPane extends FlowPane implements DriverServiceListener {
    private static final PseudoClass PSEUDO_CLASS_SELECTED = PseudoClass.getPseudoClass("selected");
    
    private DriverInterface driverData;
    @Setter @Getter
    private MainController mainController;
    
    @Nullable
    private ActivityBlock selectedBlock;
    
    private BooleanProperty createRenderProperty(boolean initial, String name) {
        return new BooleanPropertyBase(initial) {
            @Override
            public Object getBean() {
                return ActivityPane.this;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            protected void invalidated() {
                ActivityPane.this.updateAll();
            }
        };
    }
    
    @Getter private final BooleanProperty renderDayDividersProperty = createRenderProperty(true, "renderDayDividers");
    @Getter private final BooleanProperty renderWeekDividersProperty = createRenderProperty(true, "renderWeekDividers");
    @Getter private final BooleanProperty renderCardStatusProperty = createRenderProperty(true, "renderCardStatus");
    @Getter private final BooleanProperty renderSpecificConditionsProperty = createRenderProperty(true, "renderSpecificConditions");

    public ActivityPane() {
        this.setRowValignment(VPos.BOTTOM);
        this.setOnMousePressed(event -> {
            this.requestFocus();
            event.consume();
        });
        this.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton() == MouseButton.PRIMARY) {
                mouseEvent.consume();
                setSelectedBlock(-1);
            }
        });
        this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if(event.getCode() == KeyCode.LEFT) {
                mainController.onMoveBackward();
                event.consume();
            } else if(event.getCode() == KeyCode.RIGHT) {
                mainController.onMoveForward();
                event.consume();
            }
        });
        this.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.DELETE) {
                event.consume();
                getSelectedBlock().flatMap(this::getActivityBlock).ifPresent(ActivityBlock::onDeleteAction);
            }
        });
    }

    public void initialize(DriverInterface driverData) {
        this.driverData = driverData;
        driverData.addDriverServiceListener(this);
        reload(driverData.getBlocks());
    }

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
    
    public void updateAll() {
        getChildren().forEach(node -> {
            if(node instanceof ActivityBlock activityBlock) activityBlock.update();
        });
    }
    
    public void setSelectedBlock(int index) {
        if(selectedBlock != null) selectedBlock.pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, false);
        if(index >= 0 && index < getChildren().size()) {
            getActivityBlock(index).ifPresent(activityBlock -> { 
                selectedBlock = activityBlock;
                selectedBlock.pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, true);
            });
        } else {
            selectedBlock = null;
        }
    }

    public Optional<Integer> getSelectedBlock() {
        return Optional.ofNullable(selectedBlock).map(ActivityBlock::getActivityIndex);
    }

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
    
    private Optional<ActivityBlock> getActivityBlock(int index) {
        return getChildren().get(index) instanceof ActivityBlock activityBlock ? Optional.of(activityBlock) : Optional.empty();
    }

    @Override
    public void onAllActivitiesUpdated(List<Activity> activities) {
        reload(activities);
    }

    @Override
    public void onActivityRemoved(int index) {
        getChildren().remove(index);
        if(getSelectedBlock().isPresent() && index == getSelectedBlock().get()) setSelectedBlock(-1);
        updateIndices();
        // update block affected by start line change, when first block is removed
        if(index == 0 && !driverData.getBlocks().isEmpty()) {
            onActivityUpdated(0);
        }
        // update block affected by end line change, when last block is removed
        if(index == driverData.getBlocks().size() && index > 0) {
            onActivityUpdated(index-1);
        }
    }

    @Override
    public void onActivityAdded(int index, Activity activity) {
        getChildren().add(index, new ActivityBlock(this, activity, index));
        updateIndices();
        // update block affected by start line change, when first block is inserted
        if(index == 0 && driverData.getBlocks().size() > 1) {
            onActivityUpdated(1);
        }
        // update block affected by end line change, when last block is inserted
        if(index == driverData.getBlocks().size()-1 && index > 0) {
            onActivityUpdated(index-1);
        }
    }

    @Override
    public void onActivityUpdated(int index) {
        getActivityBlock(index).ifPresent(ActivityBlock::update);
    }

    @Override
    public void onActivitiesMerged(int index) {
        setSelectedBlock(-1);
        getActivityBlock(index).ifPresent(ActivityBlock::showMergeEffect);
    }
}
