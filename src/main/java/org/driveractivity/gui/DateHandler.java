package org.driveractivity.gui;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.driveractivity.entity.Activity;
import org.driveractivity.entity.ActivityType;
import org.driveractivity.service.DriverInterface;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

public class DateHandler {
    private MainController mainController;
    private ActivityType currentActivityType;
    private int insertionIndex;
    
    @FXML
    private Label errorLabel;
    @FXML
    private TextField cbHourStart;
    @FXML
    private TextField cbHourEnd;
    @FXML
    private TextField cbHourDuration;
    @FXML
    private TextField cbMinuteStart;
    @FXML
    private TextField cbMinuteEnd;
    @FXML
    private TextField cbMinuteDuration;
    @FXML
    private Button processButton;
    @FXML
    public Text DayText;

    public void initialize(MainController mainController, ActivityType activityType, int insertionIndex) {
        this.mainController = mainController;
        this.currentActivityType = activityType;
        this.insertionIndex = insertionIndex;

        errorLabel.setVisible(false);
        DriverInterface driverInterface = mainController.driverInterface;
        List<Activity> blocks = driverInterface.getBlocks();
        if(blocks.isEmpty() || insertionIndex == 0){
            cbHourStart.setText(String.valueOf(0));
            cbMinuteStart.setText(String.valueOf(0));
            cbHourStart.setDisable(false);
            cbMinuteStart.setDisable(false);
        } else {
            Activity previousActivity = blocks.get(insertionIndex - 1);
            LocalDateTime endTime = previousActivity.getEndTime();
            cbHourStart.setText(String.valueOf(endTime.getHour()));
            cbMinuteStart.setText(String.valueOf(endTime.getHour()));
        }

        cbHourEnd.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(checkforValidTime()&&!newValue){
                processStartEnd();
            }
        });
        cbMinuteEnd.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(checkforValidTime()&&!newValue){
                processStartEnd();
            }
        });

        cbHourDuration.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(checkforValidTime()&&!newValue){
                durationWithStart();
            }
        });
        cbMinuteDuration.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(checkforValidTime()&&!newValue){
                durationWithStart();
            }
        });
    }


    private boolean checkforValidTime() {
        try{
            int ValueofMituesEnd = Integer.parseInt(cbMinuteEnd.getText());
            int ValueofMituesDuration = Integer.parseInt(cbMinuteDuration.getText());
            int ValueofHoursEnd = Integer.parseInt(cbHourEnd.getText());
            int ValueofHoursDuration = Integer.parseInt(cbHourDuration.getText());

            if(ValueofMituesEnd<60&&ValueofMituesEnd>=0&&ValueofMituesDuration<60&&ValueofMituesDuration>=0){
                if(ValueofHoursEnd>=0&&ValueofHoursDuration>=0){
                    processButton.setDisable(false);
                    errorLabel.setVisible(false);
                    return true;
                }
            }
            else {
                processButton.setDisable(true);
                errorLabel.setVisible(true);
                errorLabel.setText("bitte nur eingaben zwichen 0 und 59 für Minuten");
            }
        } catch (NumberFormatException e) {
            processButton.setDisable(true);
            errorLabel.setVisible(true);
            errorLabel.setText("ungültiges Datum");
        }
        return false;
    }
            private void processStartEnd(){
            if (cbHourEnd.getText() != null && cbMinuteEnd.getText() != null ) {
                if (Integer.parseInt(cbHourStart.getText()) == Integer.parseInt(cbHourEnd.getText())) {
                    if (Integer.parseInt(cbMinuteStart.getText()) <= Integer.parseInt(cbMinuteEnd.getText())) {
                        showError("");
                        processButton.setDisable(false);
                        errorLabel.setVisible(false);
                        errorLabel.setVisible(false);
                        DayText.setText("");
                        cbHourDuration.setText(String.valueOf(Integer.parseInt(cbHourEnd.getText()) - Integer.parseInt(cbHourStart.getText())));
                        cbMinuteDuration.setText(String.valueOf(Integer.parseInt(cbMinuteEnd.getText()) - Integer.parseInt(cbMinuteStart.getText())));


                    } else {
                        showError("Start Zeit größer oder gleich Endzeit");
                        //a.showMessageDialog(null, "Start Zeit größer oder gleich Endzeit", "Something went Wrong", JOptionPane.WARNING_MESSAGE);
                    }
                } else if(Integer.parseInt(cbHourStart.getText()) < Integer.parseInt(cbHourEnd.getText())){
                    showError("");
                    processButton.setDisable(false);
                    errorLabel.setVisible(false);
                    DayText.setText("");
                    cbHourDuration.setText(String.valueOf(Integer.parseInt(cbHourEnd.getText()) - Integer.parseInt(cbHourStart.getText())));
                    if(Integer.parseInt(cbMinuteStart.getText()) < Integer.parseInt(cbMinuteEnd.getText())){
                        cbMinuteDuration.setText(String.valueOf(Integer.parseInt(cbMinuteEnd.getText()) - Integer.parseInt(cbMinuteStart.getText())));
                    }else {
                        cbMinuteDuration.setText(String.valueOf(Integer.parseInt(cbMinuteStart.getText()) - Integer.parseInt(cbMinuteEnd.getText())));
                    }

                }else {
                    showError("");
                    processButton.setDisable(false);
                    errorLabel.setVisible(false);
                    DayText.setText("Über eine Nacht");
                    cbHourDuration.setText(String.valueOf(24-Integer.parseInt(cbHourStart.getText()) + Integer.parseInt(cbHourEnd.getText())));
                    if(Integer.parseInt(cbMinuteStart.getText()) > Integer.parseInt(cbMinuteEnd.getText())){
                        cbMinuteDuration.setText(String.valueOf(60-Integer.parseInt(cbMinuteStart.getText()) - Integer.parseInt(cbMinuteEnd.getText())));
                    }else {
                        cbMinuteDuration.setText(String.valueOf(60-Integer.parseInt(cbMinuteEnd.getText()) - Integer.parseInt(cbMinuteStart.getText())));
                    }
                }
            }
    }

    public void onActionProcess() {
        int hour = Integer.parseInt(cbHourDuration.getText());
        int minute = Integer.parseInt(cbMinuteDuration.getText());

        int duration = hour*60 + minute;

        Activity activity;

        if(mainController.driverInterface.getBlocks().isEmpty()){
            LocalDateTime startTime = LocalDateTime.of(mainController.myDate,LocalTime.of( Integer.parseInt(cbHourStart.getText()),Integer.parseInt(cbHourEnd.getText())));
            activity = new Activity(currentActivityType, Duration.of(duration, ChronoUnit.MINUTES), startTime);
        }
        else {
            activity = new Activity(currentActivityType, Duration.of(duration, ChronoUnit.MINUTES), mainController.driverInterface.getBlocks().getLast().getEndTime());
        }


        mainController.activityPane.addActivity(insertionIndex, activity);

        Stage stage = (Stage) processButton.getScene().getWindow();
        stage.close();
    }


    private void showError(String error){
        if(error.isEmpty()){
            errorLabel.setVisible(false);
            processButton.setDisable(false);
        }else {
            errorLabel.setVisible(true);
            processButton.setDisable(true);
            errorLabel.setText(error);
        }
    }

    private void durationWithStart(){
        int newDruationMin = Integer.parseInt(cbMinuteDuration.getText());
        int newStartMin = Integer.parseInt(cbMinuteStart.getText());

        int newDruationHour = Integer.parseInt(cbHourDuration.getText());
        int newStartHour = Integer.parseInt(cbHourStart.getText());

            if(newDruationHour+newStartHour>24){
                int t  = (newDruationHour+newStartHour)%24;
                System.out.println(t);
                cbHourEnd.setText(String.valueOf((t)));
                DayText.setText("Über eine Nacht");
            }

            if(newDruationMin+newStartMin>=60){
                cbHourEnd.setText(String.valueOf(Integer.parseInt(cbHourEnd.getText())+1));
                cbMinuteEnd.setText(String.valueOf(Integer.parseInt(cbMinuteDuration.getText()) + Integer.parseInt(cbMinuteStart.getText())-60));
            }
            else {
                cbMinuteEnd.setText(String.valueOf(Integer.parseInt(cbMinuteDuration.getText()) + Integer.parseInt(cbMinuteStart.getText())));
            }

    }
}
