package org.driveractivity.gui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import org.driveractivity.entity.Activity;
import org.driveractivity.entity.ActivityType;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ActivityBlock extends StackPane implements Initializable {
    
    private static final URL LAYOUT_URL = ActivityBlock.class.getResource("activity-block.fxml");
    private static final DateTimeFormatter START_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .toFormatter();

    private final Activity activity;
    
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
    
    public ActivityBlock(Activity activity) {
        this.activity = activity;
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
        name.setText(formatTypeName(activity.getType()));
        startTime.setText(activity.getStartTime().format(START_TIME_FORMATTER));
        duration.setText(formatDuration(activity.getDuration()));
        this.getStyleClass().add(CSS_CLASS.get(activity.getType()));
        createDivisorLines();
        this.setOnContextMenuRequested(event -> contextMenu.show(this, event.getScreenX(), event.getScreenY()));
        contextMenuEdit.setOnAction(actionEvent -> {
            System.out.println("Edit");
        });
        contextMenuDelete.setOnAction(actionEvent -> {
            System.out.println("Delete");
        });

        setContextMenuInsertAction(contextMenuInsertBefore, 0);
        setContextMenuInsertAction(contextMenuInsertAfter, 1);
    }
    
    private void setContextMenuInsertAction(Menu menu, int shift) {
        for (MenuItem item : menu.getItems()) {
            String userData = item.getUserData().toString();
            try {
                ActivityType activityType = ActivityType.valueOf(userData.toUpperCase());
                item.setOnAction(actionEvent -> {
                    System.out.println("Adding " + activityType + " with shift " + shift);
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
        // Find all timestamps between start and end where a new day begins
        startDate.datesUntil(endDate.plusDays(1))
                .map(LocalDate::atStartOfDay)
                .filter(startOfDay -> !startOfDay.isBefore(start) && startOfDay.isBefore(end))
                .mapToLong(startOfDay -> start.until(startOfDay, ChronoUnit.MILLIS))
                .mapToDouble(millis -> ((double) millis) / durationMillis)
                .mapToObj(this::createDivisorLine)
                .forEach(dividerChildren::addFirst);
    }

    private Line createDivisorLine(double percentage) {
        Line line = new Line();
        line.getStyleClass().add("day-divider-line");
        line.endYProperty().bind(dividers.heightProperty());
        line.layoutXProperty().bind(dividers.widthProperty().multiply(percentage));
        return line;
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
