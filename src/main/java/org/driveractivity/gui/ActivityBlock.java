package org.driveractivity.gui;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import lombok.Getter;
import lombok.Setter;
import org.driveractivity.entity.Activity;
import org.driveractivity.entity.ActivityType;
import org.driveractivity.entity.SpecificCondition;
import org.driveractivity.entity.SpecificConditionType;
import org.driveractivity.util.ColorUtil;
import org.driveractivity.util.TimeUtil;
import org.kordamp.ikonli.javafx.FontIcon;

import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ActivityBlock extends StackPane {
    
    private static final DateTimeFormatter START_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_MARKER_FORMATTER_YEAR = DateTimeFormatter.ofPattern("dd.MM.yy");
    private static final DateTimeFormatter DATE_MARKER_FORMATTER_MONTH = DateTimeFormatter.ofPattern("dd.MM.");

    private final ActivityPane activityPane;
    private final Activity activity;
    @Setter @Getter
    private int activityIndex;

    private final Pane overlays = new Pane();
    private final StackPane block = new StackPane();
    private final Label typeLabel = new Label();
    private final Label durationLabel = new Label();
    private final Label startTimeLabel = new Label();
    private final FontIcon cardInsertedIcon = new FontIcon();


    private final ObservableList<Node> overlayChildren = this.overlays.getChildren();
    
    private ContextMenu contextMenu;
    
    public ActivityBlock(ActivityPane activityPane, Activity activity, int activityIndex) {
        this.activity = activity;
        this.activityPane = activityPane;
        this.activityIndex = activityIndex;
        
        this.maxWidthProperty().bind(this.prefWidthProperty());
        
        this.cardInsertedIcon.setIconSize(16);
        this.cardInsertedIcon.setIconCode(Icons.CARD_NOT_INSERTED);
        this.cardInsertedIcon.setIconColor(Color.color(0.5, 0.2, 0.2));

        AnchorPane startTimeAnchorPane = new AnchorPane(startTimeLabel, cardInsertedIcon);
        AnchorPane.setBottomAnchor(startTimeLabel, 2.0);
        AnchorPane.setLeftAnchor(startTimeLabel, 2.0);
        AnchorPane.setTopAnchor(cardInsertedIcon, 2.0);
        AnchorPane.setLeftAnchor(cardInsertedIcon, 2.0);

        VBox centerVBox = new VBox(durationLabel, typeLabel);
        centerVBox.setAlignment(Pos.CENTER);
        
        overlays.setMouseTransparent(true);
        
        block.getChildren().addAll(startTimeAnchorPane, centerVBox);
        this.getChildren().addAll(block, overlays);
        
        block.setOnContextMenuRequested(event -> getContextMenu().show(this, event.getScreenX(), event.getScreenY()));
        
        block.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton() == MouseButton.PRIMARY) {
                mouseEvent.consume();
                activityPane.setSelectedBlock(this.activityIndex);
            }
        });
        
        update();
    }
    
    public void update() {
        this.startTimeLabel.setText(activity.getStartTime().format(START_TIME_FORMATTER));
        this.durationLabel.setText(formatDuration(activity.getDuration()));
        this.typeLabel.setText(formatTypeName(activity.getType()));
        boolean cardInserted = activity.getCardStatus().equals("inserted");
        this.cardInsertedIcon.setVisible(activityPane.isRenderCardStatus() && !cardInserted);
        
        this.getStyleClass().setAll(CSS_DIMENSIONS_CLASS.get(activity.getType()));
        long hoursDuration = activity.getDuration().toHours();
        if(activity.getType() == ActivityType.REST && hoursDuration >= 24) {
            this.getStyleClass().add("activity-dimensions-" + (hoursDuration >= 45 ? "very-tall" : "tall"));
        }
        
        block.getStyleClass().setAll("activity-block-inner", CSS_STYLE_CLASS.get(activity.getType()));

        updateOverlays();
    }
    
    public ContextMenu getContextMenu() {
        if(contextMenu == null) {
            MenuItem editItem = new MenuItem("Edit", Icons.create(Icons.EDIT, 16));
            editItem.setOnAction(actionEvent -> {
                activityPane.getMainController().openDateHandlerStage(this.activity.getType(), activityIndex, this.activity);
            });

            MenuItem deleteItem = new MenuItem("Delete", Icons.create(Icons.DELETE, 16));
            deleteItem.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));
            deleteItem.setOnAction(actionEvent -> 
                    activityPane.getDriverInterface().removeBlock(activityIndex)
            );

            Menu insertBeforeItem = new Menu("Insert before", Icons.create(Icons.INSERT_BEFORE, 16));
            createInsertItems(insertBeforeItem, 0);

            Menu insertAfterItem = new Menu("Insert after", Icons.create(Icons.INSERT_AFTER, 16));
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
                activityPane.getMainController().openDateHandlerStage(type, insertionIndex, null);
            });
            menu.getItems().add(menuItem);
        }
    }
    
    private void updateOverlays() {
        LocalDateTime start = activity.getStartTime();
        LocalDateTime end = activity.getEndTime();
        LocalDate startDate = start.toLocalDate();
        LocalDate endDate = end.toLocalDate();
        overlayChildren.clear();
        
        boolean isFirstBlock = activityIndex == 0;
        boolean isLastBlock = activityIndex == activityPane.getDriverInterface().getBlocks().size()-1;
        
        if(isFirstBlock) addMarker(0, overlayChildren, startDate.format(DATE_MARKER_FORMATTER_YEAR), "start-divider-line");
        if(isLastBlock) addMarker(1, overlayChildren, endDate.format(DATE_MARKER_FORMATTER_YEAR), "end-divider-line");

        if(activityPane.isRenderDayDividers() || activityPane.isRenderWeekDividers()) {
            createDayWeekMarkers(start, end, startDate, endDate, isFirstBlock);
        }
        
        if(activityPane.isRenderSpecificConditions()) {
            createSpecificConditionMarkers(start, end);
        }
    }

    private void createSpecificConditionMarkers(LocalDateTime start, LocalDateTime end) {
        Map<SpecificConditionType.Condition, List<SpecificCondition>> byType = activityPane.getDriverInterface().getSpecificConditions().stream()
                .sorted(Comparator.comparing(SpecificCondition::getTimestamp))
                .collect(Collectors.groupingBy(specificCondition1 -> specificCondition1.getSpecificConditionType().getCondition()));
        
        record Entry(SpecificConditionType.Condition condition, LocalDateTime start, LocalDateTime end, boolean starts, boolean ends) {}
        List<Entry> entries = new ArrayList<>();
        
        for (Map.Entry<SpecificConditionType.Condition, List<SpecificCondition>> entry : byType.entrySet()) {
            SpecificConditionType.Condition condition = entry.getKey();
            // whether this section of specific condition is the actual start of that specific condition, instead of being cut off at the start of this block
            boolean starts = false;
            LocalDateTime conditionStart = start;
            for (SpecificCondition specificCondition : entry.getValue()) {
                SpecificConditionType specificConditionType = specificCondition.getSpecificConditionType();
                LocalDateTime conditionTimestamp = specificCondition.getTimestamp();
                if(!conditionTimestamp.isBefore(end)) {
                    if(!specificConditionType.isBegin()) entries.add(new Entry(condition, conditionStart, end, starts, false));
                    break;
                }
                if(conditionTimestamp.isBefore(start)) continue;
                if(specificConditionType.isBegin()) {
                    conditionStart = conditionTimestamp;
                    starts = true;
                } else {
                    entries.add(new Entry(condition, conditionStart, conditionTimestamp, starts, true));
                }
            }
        }

        for (Entry entry : entries) {
            double percentage = TimeUtil.mapInRange(entry.start(), start, end);
            double endPercentage = TimeUtil.mapInRange(entry.end(), start, end);
            double widthPercentage = endPercentage-percentage;
            Rectangle rect = new Rectangle();
            rect.heightProperty().bind(block.heightProperty());
            rect.layoutXProperty().bind(block.widthProperty().multiply(percentage));
            DoubleBinding widthBinding = block.widthProperty().multiply(widthPercentage);
            if(!entry.ends()) {
                widthBinding = widthBinding.add(1);
            }
            ObservableValue<Number> cappedWidthObservableValue = widthBinding.map(number -> Math.max(number.doubleValue(), 1.0));
            rect.widthProperty().bind(cappedWidthObservableValue);
            
            Color baseColor = switch (entry.condition) {
                case FT -> Color.DARKBLUE;
                case OUT_OF_SCOPE -> Color.color(0.7, 0.2, 0.8);
            };
            Color color = ColorUtil.withOpacity(baseColor, 0.7);
            Color transparentColor = ColorUtil.withOpacity(baseColor, 0.2);

            rect.fillProperty().bind(rect.widthProperty().map(width -> {
                boolean starts = entry.starts();
                boolean ends = entry.ends();
                // to make very small specific conditions render properly
                boolean hasSufficientWidth = width.doubleValue() > 3;
                if(!hasSufficientWidth) return color;
                if(!starts && !ends) return transparentColor;
                
                double maxGradientPercentage = 10.0 / width.doubleValue();
                if(starts && ends) maxGradientPercentage = Math.min(maxGradientPercentage, 0.499);
                
                List<Stop> stops = new ArrayList<>(4);
                
                if(starts) {
                    stops.add(new Stop(0, color));
                    stops.add(new Stop(maxGradientPercentage, transparentColor));
                }
                if(ends) {
                    stops.add(new Stop(1.0 - maxGradientPercentage, transparentColor));
                    stops.add(new Stop(1, color));
                }
                return new LinearGradient(0,0,1,0,true, CycleMethod.NO_CYCLE, stops);
            }));

            overlayChildren.add(rect);

            Stream.of(rect.layoutYProperty(), rect.layoutYProperty().add(rect.heightProperty()).subtract(1)).forEach(yProperty -> {
                Line line = new Line();
                line.setStroke(color);
                line.layoutXProperty().bind(rect.layoutXProperty());
                line.layoutYProperty().bind(yProperty);
                line.endXProperty().bind(rect.widthProperty().subtract(1));
                overlayChildren.add(line);
            });
        }
    }

    private void createDayWeekMarkers(LocalDateTime start, LocalDateTime end, LocalDate startDate, LocalDate endDate, boolean isFirstBlock) {
        // Find all timestamps between start and end where a new day begins
        List<LocalDateTime> newDayTimes = startDate.datesUntil(endDate.plusDays(1))
                .map(LocalDate::atStartOfDay)
                .filter(startOfDay -> TimeUtil.isBetween(startOfDay, start, end))
                .filter(startOfDay -> !isFirstBlock || startOfDay.isAfter(start))
                .toList();

        boolean renderWeekDividers = activityPane.isRenderWeekDividers();
        boolean renderDayDividers = activityPane.isRenderDayDividers();

        for (LocalDateTime newDayTime : newDayTimes) {
            double percentage = TimeUtil.mapInRange(newDayTime, start, end);
            LocalDate date = newDayTime.toLocalDate();
            boolean isStartOfWeek = date.getDayOfWeek() == DayOfWeek.MONDAY;
            if(!isStartOfWeek && !renderDayDividers) continue;
            String dateLabel = null;
            if(date.getDayOfMonth() == 1) dateLabel = date.format(date.getMonth() == Month.JANUARY ? DATE_MARKER_FORMATTER_YEAR : DATE_MARKER_FORMATTER_MONTH);
            String styleClass = (renderWeekDividers && isStartOfWeek ? "week" : "day") + "-divider-line";
            addMarker(percentage, overlayChildren, dateLabel, styleClass);
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

    public void showMergeEffect() {
        Timeline timeline = new Timeline();

        KeyValue opacityStart = new KeyValue(block.opacityProperty(), 0.8, Interpolator.EASE_OUT);
        KeyValue opacityEnd = new KeyValue(block.opacityProperty(), 1, Interpolator.EASE_IN);
        KeyValue widthStart = new KeyValue(this.prefWidthProperty(), this.getPrefWidth()*2, Interpolator.EASE_OUT);
        KeyValue widthEnd = new KeyValue(this.prefWidthProperty(), this.getPrefWidth(), Interpolator.SPLINE(0.5, 1, 0.5, 1));
        
        timeline.getKeyFrames().addAll(
                new KeyFrame(javafx.util.Duration.millis(0), opacityStart, widthStart),
                new KeyFrame(javafx.util.Duration.millis(1000), opacityEnd, widthEnd)
        );
        timeline.play();
    }
}
