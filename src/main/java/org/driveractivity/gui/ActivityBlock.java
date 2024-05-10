package org.driveractivity.gui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
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
import java.time.temporal.TemporalField;
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
        this.getStyleClass().add(getCssClassForType(activity.getType()));
        createDivisorLines();
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

    private static String getCssClassForType(ActivityType type) {
        return "activity-block-" + type.name().toLowerCase();
    }
}
