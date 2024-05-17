package org.driveractivity.gui;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.*;
import javafx.stage.Stage;
import org.driveractivity.entity.Activity;
import org.driveractivity.entity.ActivityType;

import java.net.URL;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

public class DateHandler implements Initializable {
    static MainController mainController;
    ActivityType currentActivityType;
    
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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorLabel.setVisible(false);
        System.out.println(mainController.activityPane.getChildren().getLast());
        if(mainController.activityPane.getChildren().isEmpty()){
            cbHourStart.setText(String.valueOf(0));
            cbMinuteStart.setText(String.valueOf(0));
        }else {
            cbHourStart.setText(String.valueOf(mainController.driverInterface.getBlocks().getLast().getEndTime().getHour()));
            cbMinuteStart.setText(String.valueOf(mainController.driverInterface.getBlocks().getLast().getEndTime().getHour()));
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



    private boolean checkForValidDuration() {
        return true;
    }
        private void processStartEnd(){
            if (cbHourEnd.getText() != null && cbMinuteEnd.getText() != null ) {
                if (Integer.parseInt(cbHourStart.getText()) == Integer.parseInt(cbHourEnd.getText())) {
                    if (Integer.parseInt(cbMinuteStart.getText()) <= Integer.parseInt(cbMinuteEnd.getText())) {
                        showError("");
                        processButton.setDisable(false);
                        errorLabel.setVisible(false);

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
                    cbHourDuration.setText(String.valueOf(Integer.parseInt(cbHourEnd.getText()) - Integer.parseInt(cbHourStart.getText())));
                    if(Integer.parseInt(cbMinuteStart.getText()) > Integer.parseInt(cbMinuteEnd.getText())){
                        cbMinuteDuration.setText(String.valueOf(Integer.parseInt(cbMinuteStart.getText()) - Integer.parseInt(cbMinuteEnd.getText())));
                    }else {
                        cbMinuteDuration.setText(String.valueOf(Integer.parseInt(cbMinuteEnd.getText()) - Integer.parseInt(cbMinuteStart.getText())));
                    }
                }
            }
    }

    public void onActionProcess() {
        int hour = Integer.parseInt(cbHourDuration.getText());
        int minute = Integer.parseInt(cbMinuteDuration.getText());

        int duration = hour*60 + minute;


        Activity activity = new Activity(currentActivityType, Duration.of(duration, ChronoUnit.MINUTES), mainController.driverInterface.getBlocks().getLast().getEndTime());
        checkAcitvi(activity);
        mainController.driverInterface.addBlock(activity);
        mainController.activityPane.addBack(activity);

        Stage stage = (Stage) processButton.getScene().getWindow();
        stage.close();
    }

    private void checkAcitvi(Activity activity) {
        System.out.println(activity.getStartTime());
        System.out.println(activity.getDuration());
        System.out.println(activity.getEndTime());
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
        if(checkForValidDuration()) {
            int newDruationMin = Integer.parseInt(cbMinuteDuration.getText());
            int newStartMin = Integer.parseInt(cbMinuteStart.getText());
            if(newDruationMin+newStartMin>=60){
                cbHourEnd.setText(String.valueOf(1+Integer.parseInt(cbHourDuration.getText()) + Integer.parseInt(cbHourStart.getText())));
                cbMinuteEnd.setText(String.valueOf(Integer.parseInt(cbMinuteDuration.getText()) + Integer.parseInt(cbMinuteStart.getText())-60));
            }
            else {
                cbHourEnd.setText(String.valueOf(Integer.parseInt(cbHourDuration.getText()) + Integer.parseInt(cbHourStart.getText())));
                cbMinuteEnd.setText(String.valueOf(Integer.parseInt(cbMinuteDuration.getText()) + Integer.parseInt(cbMinuteStart.getText())));
            }


        }
    }
}
