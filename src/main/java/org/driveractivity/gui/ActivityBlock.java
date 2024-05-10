package org.driveractivity.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.driveractivity.entity.Activity;
import org.driveractivity.entity.ActivityType;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

public class ActivityBlock extends StackPane {
    
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
    
    public ActivityBlock(Activity activity) {
        FXMLLoader loader = new FXMLLoader(LAYOUT_URL);
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.activity = activity;
        name.setText(formatTypeName(activity.getType()));
        startTime.setText(activity.getStartTime().format(START_TIME_FORMATTER));
        duration.setText(formatDuration(activity.getDuration()));
        this.getStyleClass().add(getCssClassForType(activity.getType()));
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
