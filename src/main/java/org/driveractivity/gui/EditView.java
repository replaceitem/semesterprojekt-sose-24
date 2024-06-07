package org.driveractivity.gui;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.driveractivity.entity.Activity;
import org.driveractivity.entity.ActivityType;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class EditView {

    public TextField startTimeHour;
    public TextField startTimeMinute;
    public TextField endTimeHour;
    public TextField endTimeMinute;
    public TextField durationHour;
    public TextField durationMinute;
    public Button buttonSubmit;
    public ChoiceBox<ActivityType> activityType;
    public Label errorMessage;
    public TextField multipleDays;

    MainController mainController;
    Activity activity;
    private int activityIndex;

    public void initialize(MainController mainController, Activity activity, int activityIndex){
        this.mainController = mainController;
        this.activity = activity;
        this.activityIndex = activityIndex;

        activityType.getItems().addAll(ActivityType.values());
        activityType.setValue(activity.getType());

        startTimeHour.setText(String.valueOf(activity.getStartTime().getHour()));
        startTimeMinute.setText(String.valueOf(activity.getStartTime().getMinute()));
        startTimeHour.setDisable(true);
        startTimeMinute.setDisable(true);

        endTimeHour.setText(String.valueOf(activity.getEndTime().getHour()));
        endTimeMinute.setText(String.valueOf(activity.getEndTime().getMinute()));

        durationHour.setText(String.valueOf(activity.getDuration().toHours()));
        durationMinute.setText(String.valueOf(activity.getDuration().toMinutesPart()));

        endTimeHour.focusedProperty().addListener((observable, oldValue, newValue) -> {
            computeDuration();
        });
        endTimeHour.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("\\d*")){
                endTimeHour.setText(oldValue);
            }
        });

        endTimeMinute.focusedProperty().addListener((observable, oldValue, newValue) -> {
            computeDuration();
        });
        endTimeMinute.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("\\d*")){
                endTimeMinute.setText(oldValue);
            }
        });

        durationHour.focusedProperty().addListener((observable, oldValue, newValue) -> {
            computeEndTime();
        });
        durationHour.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("\\d*")){
                durationHour.setText(oldValue);
            }
        });

        durationMinute.focusedProperty().addListener((observable, oldValue, newValue) -> {
            computeEndTime();
        });
        durationMinute.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("\\d*")){
                durationMinute.setText(oldValue);
            }
        });

    }

    public void onSubmit(){

        int duration = Integer.parseInt(durationHour.getText()) *60 + Integer.parseInt(durationMinute.getText());
        
        Activity newActivity = Activity.builder()
                .startTime(activity.getStartTime())
                .duration(Duration.of(duration, ChronoUnit.MINUTES))
                .type(activityType.getValue())
                .build();
        
        mainController.driverInterface.changeBlock(activityIndex, newActivity);
        
        Stage stage = (Stage) buttonSubmit.getScene().getWindow();
        stage.close();
    }

    private void computeDuration(){

        LocalTime startTime;
        LocalTime endTime;

        if(checkHour(startTimeHour.getText()) && checkHour(endTimeHour.getText()) && checkMinutes(startTimeMinute.getText()) && checkMinutes(endTimeMinute.getText())) {
            startTime = LocalTime.of(Integer.parseInt(startTimeHour.getText()), Integer.parseInt(startTimeMinute.getText()));
            endTime = LocalTime.of(Integer.parseInt(endTimeHour.getText()), Integer.parseInt(endTimeMinute.getText()));

            if(startTime.isBefore(endTime)){
                Duration duration =  Duration.between(startTime, endTime);
                durationHour.setText(String.valueOf(duration.toHours()));
                durationMinute.setText(String.valueOf(duration.toMinutesPart()));
            }else{
                errorMessage.setVisible(true);
                errorMessage.setText("End Time must be before Start Time");
            }
        }else{
            errorMessage.setVisible(true);
            errorMessage.setText("Wrong Time Format!");
        }


    }

    private void computeEndTime(){

        LocalTime startTime;

        if(checkHour(startTimeHour.getText()) && checkMinutes(startTimeMinute.getText())){

            startTime = LocalTime.of(Integer.parseInt(startTimeHour.getText()), Integer.parseInt(startTimeMinute.getText()));
            int calcDuration = Integer.parseInt(durationHour.getText()) *60 + Integer.parseInt(durationMinute.getText());

            Duration duration = Duration.of(calcDuration, ChronoUnit.MINUTES);

            LocalTime endTime = startTime.plus(duration);
            endTimeHour.setText(String.valueOf(endTime.getHour()));
            endTimeMinute.setText(String.valueOf(endTime.getMinute()));

        }else{
            errorMessage.setText("Wrong Time Format!");
        }


    }

    private Boolean checkHour(String hours){
        return Integer.parseInt(hours) >= 0 && Integer.parseInt(hours) <= 24;
    }

    private Boolean checkMinutes(String minutes){
        return Integer.parseInt(minutes) >= 0 && Integer.parseInt(minutes) <= 60;
    }
}
