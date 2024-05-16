package org.driveractivity.gui;

import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.util.Callback;
import lombok.Getter;
import lombok.Setter;
import org.driveractivity.entity.Activity;
import org.driveractivity.entity.ActivityType;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ActivityBlock extends StackPane implements Initializable {
    
    private static final URL LAYOUT_URL = ActivityBlock.class.getResource("activity-block.fxml");
    private static final DateTimeFormatter START_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .toFormatter();
    
    private static final DateTimeFormatter DATE_MARKER_FORMATTER_YEAR = DateTimeFormatter.ofPattern("dd.MM.yy");
    private static final DateTimeFormatter DATE_MARKER_FORMATTER_MONTH = DateTimeFormatter.ofPattern("dd.MM.");
    private static final DateTimeFormatter DATE_MARKER_FORMATTER_DAY = DateTimeFormatter.ofPattern("dd.");

    private final Activity activity;
    @Getter @Setter
    private int activityIndex;
    private final ActivityDisplay display;

    @FXML
    public Label name;
    @FXML
    public Label startTime;
    @FXML
    public Label duration;
    @FXML
    public Pane dividers;
    @FXML
    public ContextMenu contextMenu;
    @FXML
    public MenuItem contextMenuEdit;
    @FXML
    public MenuItem contextMenuDelete;
    @FXML
    public Menu contextMenuInsertBefore;
    @FXML
    public Menu contextMenuInsertAfter;
    
    public ActivityBlock(ActivityDisplay display, Activity activity, int activityIndex) {
        this.activity = activity;
        this.display = display;
        this.activityIndex = activityIndex;
        FXMLLoader loader = new FXMLLoader(LAYOUT_URL);
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reload();
        this.setOnContextMenuRequested(event -> contextMenu.show(this, event.getScreenX(), event.getScreenY()));
        contextMenuEdit.setOnAction(actionEvent -> {
            System.out.println("Edit");
        });
        contextMenuDelete.setOnAction(actionEvent -> {
            display.removeActivity(activityIndex);
        });

        setContextMenuInsertAction(contextMenuInsertBefore, 0);
        setContextMenuInsertAction(contextMenuInsertAfter, 1);
    }
    
    public void reload() {
        name.setText(formatTypeName(activity.getType()));
        startTime.setText(activity.getStartTime().format(START_TIME_FORMATTER));
        duration.setText(formatDuration(activity.getDuration()));
        String cssClass = CSS_CLASS.get(activity.getType());
        if(!getStyleClass().contains(cssClass)) {
            this.getStyleClass().removeIf(string -> string.contains("activity-"));
            this.getStyleClass().add(cssClass);
        }
        createDivisorLines();
    }
    
    private void setContextMenuInsertAction(Menu menu, int shift) {
        for (MenuItem item : menu.getItems()) {
            String userData = item.getUserData().toString();
            try {
                ActivityType activityType = ActivityType.valueOf(userData.toUpperCase());
                item.setOnAction(actionEvent -> {
                    // TODO: test data, open dialog instead
                    display.addActivity(this.activityIndex + shift, new Activity(activityType, Duration.ofHours(3), LocalDateTime.now()));
                });
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid userData does not match an ActivityType: " + userData);
            }
        }
    }
    
    private void createDivisorLines() {
        LocalDateTime start = activity.getStartTime();
        LocalDateTime end = activity.getEndTime();
        long durationMillis = activity.getDuration().toMillis();
        LocalDate startDate = start.toLocalDate();
        LocalDate endDate = end.toLocalDate();
        ObservableList<Node> dividerChildren = this.dividers.getChildren();
        dividerChildren.clear();
        // Find all timestamps between start and end where a new day begins
        List<LocalDateTime> newDayTimes = startDate.datesUntil(endDate.plusDays(1))
                .map(LocalDate::atStartOfDay)
                .filter(startOfDay -> !startOfDay.isBefore(start) && startOfDay.isBefore(end))
                .toList();

        for (LocalDateTime newDayTime : newDayTimes) {
            LocalDate date = newDayTime.toLocalDate();
            long millisAfterStart = start.until(newDayTime, ChronoUnit.MILLIS);
            double blockPercentage = ((double) millisAfterStart) / durationMillis;
            boolean isInitial = newDayTime.isBefore(display.getStartTime().plusDays(1));

            Line line = new Line();
            line.getStyleClass().add("day-divider-line");
            line.endYProperty().bind(this.heightProperty());
            line.layoutXProperty().bind(this.widthProperty().multiply(blockPercentage));
            dividerChildren.add(line);

            DateTimeFormatter markerFormatter = getMarkerFormatter(date, isInitial);
            Label label = new Label(date.format(markerFormatter));
            label.layoutXProperty().bind(line.layoutXProperty().subtract(label.widthProperty().divide(2)));
            label.layoutYProperty().bind(line.layoutYProperty().subtract(label.heightProperty()));
            dividerChildren.add(label);
        }
    }
    
    private DateTimeFormatter getMarkerFormatter(LocalDate date, boolean first) {
        // Omit fields when they are the same as the previous marker or the very first one
        if(activityIndex == 0 && first) return DATE_MARKER_FORMATTER_YEAR;
        if(date.getDayOfMonth() == 1) return date.getMonth() == Month.JANUARY ? DATE_MARKER_FORMATTER_YEAR : DATE_MARKER_FORMATTER_MONTH;
        return DATE_MARKER_FORMATTER_DAY;
    }

    private static String formatTypeName(ActivityType type) {
        String lowerCase = type.name().toLowerCase();
        return lowerCase.substring(0, 1).toUpperCase() + lowerCase.substring(1);
    }

    private static String formatDuration(Duration duration) {
        if(duration.toHours() == 0) {
            return duration.toMinutes() + "m";
        } else {
            float decimalHours = ((float) duration.getSeconds()) / ChronoUnit.HOURS.getDuration().getSeconds();
            return String.format("%.01fh", decimalHours);
        }
    }
    
    private static final Map<ActivityType,String> CSS_CLASS = new EnumMap<>(Map.of(
            ActivityType.REST, "activity-rest",
            ActivityType.DRIVING, "activity-drive",
            ActivityType.WORK, "activity-work",
            ActivityType.AVAILABLE, "activity-available"
    ));
}
