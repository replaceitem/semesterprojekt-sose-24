package org.driveractivity.gui;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.driveractivity.entity.Activity;
import org.driveractivity.entity.ActivityType;
import org.driveractivity.service.DriverInterface;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

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
    private LocalDate myDate;
    public void initialize(MainController mainController, ActivityType activityType, int insertionIndex) {
        this.mainController = mainController;
        this.currentActivityType = activityType;
        this.insertionIndex = insertionIndex;

        errorLabel.setVisible(false);
        DriverInterface driverInterface = mainController.driverInterface;
        List<Activity> blocks = driverInterface.getBlocks();
        if(blocks.isEmpty() || insertionIndex == 0){

            openDateTimePickerDialog();
            if(myDate.equals(null)){
                Stage stage = (Stage) processButton.getScene().getWindow();
                stage.close();
            }
            cbHourStart.setText(String.valueOf(0));
            cbMinuteStart.setText(String.valueOf(0));
            cbHourStart.setDisable(false);
            cbMinuteStart.setDisable(false);
        } else {
            Activity previousActivity = blocks.get(insertionIndex - 1);
            LocalDateTime endTime = previousActivity.getEndTime();
            cbHourStart.setText(String.valueOf(endTime.getHour()));
            cbMinuteStart.setText(String.valueOf(endTime.getMinute()));
        }

        cbHourStart.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(checkforValidTime()&&!newValue){
                processStartEnd();
            }
        });
        cbMinuteStart.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(checkforValidTime()&&!newValue){
                processStartEnd();
            }
        });

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
            int ValueofHoursStart = Integer.parseInt(cbHourStart.getText());
            int ValueofMinutesStart = Integer.parseInt(cbMinuteStart.getText());

            if(ValueofMituesEnd<60&&ValueofMituesEnd>=0&&ValueofMituesDuration<60&&ValueofMituesDuration>=0&&ValueofMinutesStart<60&&ValueofMinutesStart>=0){
                if((ValueofHoursEnd >= 0) && (ValueofHoursDuration >= 0) && (ValueofHoursStart >= 0)) {
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

        Activity.ActivityBuilder activityBuilder = Activity.builder()
                .type(currentActivityType)
                .duration(Duration.of(duration, ChronoUnit.MINUTES));

        if(mainController.driverInterface.getBlocks().isEmpty()){
            LocalDateTime startTime = LocalDateTime.of(myDate,LocalTime.of( Integer.parseInt(cbHourStart.getText()),Integer.parseInt(cbHourEnd.getText())));
            activityBuilder = activityBuilder.startTime(startTime);
            mainController.driverInterface.addBlock(activityBuilder.build());
        } else {
            activityBuilder = activityBuilder.startTime(mainController.driverInterface.getBlocks().getLast().getEndTime());
            mainController.driverInterface.addBlock(insertionIndex, activityBuilder.build());
        }
        
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
    private void openDateTimePickerDialog() {
        Dialog<LocalDateTime> dialog = new Dialog<>();
        dialog.setTitle("DateTime Picker");
        dialog.setHeaderText("Select Date and Time");

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        DatePicker datePicker = new DatePicker(LocalDate.now());

        Spinner<Integer> hourSpinner = new Spinner<>();
        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));

        Spinner<Integer> minuteSpinner = new Spinner<>();
        minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));

        grid.add(new Label("Date:"), 0, 0);
        grid.add(datePicker, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                LocalDate date = datePicker.getValue();
                LocalTime time = LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue());
                return LocalDateTime.of(date, time);
            }
            return null;
        });

        Optional<LocalDateTime> result = dialog.showAndWait();
        result.ifPresent(dateTime -> myDate= LocalDate.from(dateTime));
    }
}
