package org.driveractivity.gui;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import org.driveractivity.entity.Activity;
import org.driveractivity.entity.ActivityType;
import org.kordamp.ikonli.javafx.FontIcon;

import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ActivityBlock extends StackPane {
    
    private static final DateTimeFormatter START_TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm");
    
    private static final DateTimeFormatter DATE_MARKER_FORMATTER_YEAR = DateTimeFormatter.ofPattern("dd.MM.yy");
    private static final DateTimeFormatter DATE_MARKER_FORMATTER_MONTH = DateTimeFormatter.ofPattern("dd.MM.");

    private final ActivityPane activityPane;
    private final Activity activity;
    private final int activityIndex;

    private final Pane overlays = new Pane();
    private final StackPane block = new StackPane();
    private final Label typeLabel = new Label();
    private final Label durationLabel = new Label();
    private final Label startTimeLabel = new Label();
    
    private ContextMenu contextMenu;
    
    public ActivityBlock(ActivityPane activityPane, Activity activity, int activityIndex) {
        this.activity = activity;
        this.activityPane = activityPane;
        this.activityIndex = activityIndex;

        AnchorPane startTimeAnchorPane = new AnchorPane(startTimeLabel);
        AnchorPane.setBottomAnchor(startTimeLabel, 2.0);
        AnchorPane.setLeftAnchor(startTimeLabel, 2.0);

        VBox centerVBox = new VBox(durationLabel, typeLabel);
        centerVBox.setAlignment(Pos.CENTER);
        
        overlays.setMouseTransparent(true);
        
        block.getChildren().addAll(startTimeAnchorPane, centerVBox);
        this.getChildren().addAll(block, overlays);
        
        block.setOnContextMenuRequested(event -> getContextMenu().show(this, event.getScreenX(), event.getScreenY()));
        
        update();
    }
    
    public void update() {
        this.startTimeLabel.setText(activity.getStartTime().format(START_TIME_FORMATTER));
        this.durationLabel.setText(formatDuration(activity.getDuration()));
        this.typeLabel.setText(formatTypeName(activity.getType()));
        
        this.getStyleClass().setAll(CSS_DIMENSIONS_CLASS.get(activity.getType()));
        long hoursDuration = activity.getDuration().toHours();
        if(activity.getType() == ActivityType.REST && hoursDuration >= 24) {
            this.getStyleClass().add("activity-dimensions-" + (hoursDuration >= 45 ? "very-tall" : "tall"));
        }
        
        block.getStyleClass().setAll("activity-block-inner", CSS_STYLE_CLASS.get(activity.getType()));

        updateDivisorLines();
    }
    
    public static FontIcon createIcon(String name) {
        FontIcon icon = new FontIcon(name);
        icon.setIconSize(16);
        return icon;
    }
    
    public ContextMenu getContextMenu() {
        if(contextMenu == null) {
            MenuItem editItem = new MenuItem("Edit", createIcon("fth-edit"));
            editItem.setOnAction(actionEvent -> {
                System.out.println("Edit");
                // TODO
            });

            MenuItem deleteItem = new MenuItem("Delete", createIcon("fth-trash"));
            deleteItem.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));
            deleteItem.setOnAction(actionEvent -> {
                activityPane.removeActivity(activityIndex);
            });

            Menu insertBeforeItem = new Menu("Insert before", createIcon("fth-chevron-left"));
            createInsertItems(insertBeforeItem, 0);

            Menu insertAfterItem = new Menu("Insert after", createIcon("fth-chevron-right"));
            createInsertItems(insertAfterItem, 1);

            contextMenu = new ContextMenu(editItem, deleteItem, insertBeforeItem, insertAfterItem);
        }
        return contextMenu;
    }

    private void createInsertItems(Menu menu, int shift) {
        for (ActivityType type : ActivityType.values()) {
            MenuItem menuItem = new MenuItem(formatTypeName(type));
            menuItem.getStyleClass().add(CSS_STYLE_CLASS.get(type));
            menuItem.setOnAction(actionEvent -> {
                int insertionIndex = this.activityIndex + shift;
                activityPane.getMainController().openDateHandlerStage(type, insertionIndex);
            });
            menu.getItems().add(menuItem);
        }
    }
    
    private void updateDivisorLines() {
        LocalDateTime start = activity.getStartTime();
        LocalDateTime end = activity.getEndTime();
        long durationMillis = activity.getDuration().toMillis();
        LocalDate startDate = start.toLocalDate();
        LocalDate endDate = end.toLocalDate();
        ObservableList<Node> dividerChildren = this.overlays.getChildren();
        dividerChildren.clear();
        
        boolean isFirstBlock = activityIndex == 0;
        boolean isLastBlock = activityIndex == activityPane.getDriverInterface().getBlocks().size()-1;
        // Find all timestamps between start and end where a new day begins
        List<LocalDateTime> newDayTimes = startDate.datesUntil(endDate.plusDays(1))
                .map(LocalDate::atStartOfDay)
                .filter(startOfDay -> (isFirstBlock ? startOfDay.isAfter(start) : !startOfDay.isBefore(start)) && startOfDay.isBefore(end))
                .toList();
        
        if(isFirstBlock) {
            addMarker(0, dividerChildren, startDate.format(DATE_MARKER_FORMATTER_YEAR), "start-divider-line");
        } else if(isLastBlock) {
            addMarker(1, dividerChildren, endDate.format(DATE_MARKER_FORMATTER_YEAR), "end-divider-line");
        }

        for (LocalDateTime newDayTime : newDayTimes) {
            LocalDate date = newDayTime.toLocalDate();
            long millisAfterStart = start.until(newDayTime, ChronoUnit.MILLIS);
            double blockPercentage = ((double) millisAfterStart) / durationMillis;
            String dateLabel = null;
            if(date.getDayOfMonth() == 1) dateLabel = date.format(date.getMonth() == Month.JANUARY ? DATE_MARKER_FORMATTER_YEAR : DATE_MARKER_FORMATTER_MONTH);
            String styleClass = (date.getDayOfWeek() == DayOfWeek.MONDAY ? "week" : "day") + "-divider-line";
            addMarker(blockPercentage, dividerChildren, dateLabel, styleClass);
        }
    }
    
    private void addMarker(double percentage, List<Node> nodes, String labelText, String styleClass) {
        Line line = new Line();
        line.getStyleClass().addAll(styleClass, "divider-line");
        line.endYProperty().bind(block.heightProperty());
        line.layoutXProperty().bind(block.widthProperty().multiply(percentage));
        nodes.add(line);
        
        if(labelText != null) {
            Label label = new Label(labelText);
            label.layoutXProperty().bind(line.layoutXProperty());
            label.layoutYProperty().bind(line.layoutYProperty().subtract(label.heightProperty()));
            nodes.add(label);
        }
    }

    private static String formatTypeName(ActivityType type) {
        String lowerCase = type.name().toLowerCase();
        return lowerCase.substring(0, 1).toUpperCase() + lowerCase.substring(1);
    }
    
    private final static DecimalFormat DURATION_HOURS_DECIMAL_FORMAT = new DecimalFormat("0.#h");

    private static String formatDuration(Duration duration) {
        if(duration.toHours() == 0) {
            return duration.toMinutes() + "m";
        } else {
            float decimalHours = ((float) duration.getSeconds()) / ChronoUnit.HOURS.getDuration().getSeconds();
            return DURATION_HOURS_DECIMAL_FORMAT.format(decimalHours);
        }
    }
    
    private static final Map<ActivityType,String> CSS_STYLE_CLASS = new EnumMap<>(Map.of(
            ActivityType.REST, "activity-rest",
            ActivityType.DRIVING, "activity-drive",
            ActivityType.WORK, "activity-work",
            ActivityType.AVAILABLE, "activity-available"
    ));
    private static final Map<ActivityType,String> CSS_DIMENSIONS_CLASS = new EnumMap<>(Map.of(
            ActivityType.REST, "activity-dimensions-rest",
            ActivityType.DRIVING, "activity-dimensions-drive",
            ActivityType.WORK, "activity-dimensions-work",
            ActivityType.AVAILABLE, "activity-dimensions-available"
    ));
}
