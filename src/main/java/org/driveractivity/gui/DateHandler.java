package org.driveractivity.gui;

import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.util.*;
import javafx.util.converter.*;
import org.driveractivity.entity.*;
import org.driveractivity.service.*;

import java.time.Duration;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.stream.*;

public class DateHandler {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    
    private MainController mainController;
    private int insertionIndex;
    
    // converter that prevents number format exceptions and clamps values in bounds
    private static IntegerStringConverter createSafeConverter(SpinnerValueFactory<Integer> factory) {
        if(!(factory instanceof SpinnerValueFactory.IntegerSpinnerValueFactory integerFactory)) throw new IllegalArgumentException("Must be an integer spinner factory");
        int min = integerFactory.getMin();
        int max = integerFactory.getMax();
        return new IntegerStringConverter() {
            @Override
            public Integer fromString(String s) {
                try {
                    return Math.max(Math.min(super.fromString(s), max), min);
                } catch (NumberFormatException nfe) {
                    return 0;
                }
            }
        };
    }
    
    @FXML
    private Label errorLabel;
    @FXML
    private Spinner<Integer> cbHourStart;
    @FXML
    private Spinner<Integer> cbHourEnd;
    @FXML
    private Spinner<Integer> cbHourDuration;
    @FXML
    private Spinner<Integer> cbMinuteStart;
    @FXML
    private Spinner<Integer> cbMinuteEnd;
    @FXML
    private Spinner<Integer> cbMinuteDuration;


    @FXML
    public Label startDateLabel;
    @FXML
    public Label endDateLabel;
    
    @FXML
    private Button processButton;
    @FXML
    public Text DayText;
    @FXML
    public CheckBox cardInserted;
    @FXML
    public ChoiceBox<ActivityType> activityTypeChoiceBox;
    
    private LocalDate myDate;
    private LocalDateTime previousEnd;
    
    public void initialize(MainController mainController, ActivityType activityType, int insertionIndex) {
        this.mainController = mainController;
        this.insertionIndex = insertionIndex;
        
        activityTypeChoiceBox.getItems().addAll(ActivityType.values());
        activityTypeChoiceBox.setValue(activityType);
        errorLabel.setVisible(false);
        DriverInterface driverInterface = mainController.driverInterface;
        List<Activity> blocks = driverInterface.getBlocks();
        
        LocalTime startTime;
        if(blocks.isEmpty() || insertionIndex == 0){

            openDateTimePickerDialog();
            if(myDate == null){
                Stage stage = (Stage) processButton.getScene().getWindow();
                stage.close();
                return;
            }
            startTime = LocalTime.MIDNIGHT;
            cbHourStart.setDisable(false);
            cbMinuteStart.setDisable(false);
        } else {
            Activity previousActivity = blocks.get(insertionIndex - 1);
            myDate = previousActivity.getEndTime().toLocalDate();
            LocalDateTime endTime = previousActivity.getEndTime();
            startTime = endTime.toLocalTime();
        }
        
        Duration initialDuration = Duration.ofHours(1);
        previousEnd = LocalDateTime.of(myDate, startTime.plus(initialDuration));
        setStartTime(startTime);
        setDuration(initialDuration);
        setEndime(startTime.plus(initialDuration));
        
        startDateLabel.setText(myDate.format(DATE_FORMATTER));
        endDateLabel.setText(previousEnd.toLocalDate().format(DATE_FORMATTER));

        Stream.of(cbHourStart, cbMinuteStart, cbHourEnd, cbMinuteEnd, cbHourDuration, cbMinuteDuration)
                .forEach(spinner -> {
                    spinner.getValueFactory().setConverter(createSafeConverter(spinner.getValueFactory()));
                    spinner.focusedProperty().addListener((observable,before,after) -> {
                        if(!after) { // make sure the edit box is properly reset when invalid numbers are entered
                            StringConverter<Integer> converter = spinner.getValueFactory().getConverter();
                            int validated = converter.fromString(spinner.getEditor().getText());
                            spinner.getEditor().setText(converter.toString(validated));
                        }
                    });
                });

        cbHourStart.valueProperty().addListener((observable, oldValue, newValue) -> onStartTimeChanged());
        cbMinuteStart.valueProperty().addListener((observable, oldValue, newValue) -> onStartTimeChanged());
        
        cbHourDuration.valueProperty().addListener((observable, oldValue, newValue) -> onDurationChanged());
        cbMinuteDuration.valueProperty().addListener((observable, oldValue, newValue) -> onDurationChanged());

        cbHourEnd.valueProperty().addListener((observable, oldValue, newValue) -> onEndTimeChanged());
        cbMinuteEnd.valueProperty().addListener((observable, oldValue, newValue) -> onEndTimeChanged());
    }

    // prevent recursive updates, which can happen because the two spinners are set after each other,
    // but listeners will trigger after the first one with incomplete values, which would propagate
    private boolean isUpdating = false;
    
    private void onStartTimeChanged() {
        // Do the same thing as if end time changed, meaning updating duration
        onEndTimeChanged();
    }
    
    private void onEndTimeChanged() {
        if(!isUpdating) {
            try {
                isUpdating = true;
                LocalTime startTime = getStartTime();
                LocalTime endTime = getEndTime();
                LocalDateTime startDateTime = LocalDateTime.of(myDate, startTime);
                // keep the same date when editing end time
                LocalDateTime sameDateEndTime = LocalDateTime.of(previousEnd.toLocalDate(), endTime);
                setDuration(Duration.between(startDateTime, sameDateEndTime));
            } finally {
                isUpdating = false;
            }
        }
    }

    private void onDurationChanged() {
        if(!isUpdating) {
            try {
                isUpdating = true;
                LocalTime startTime = getStartTime();
                Duration duration = getDuration();
                LocalDateTime endDateTime = LocalDateTime.of(myDate, startTime).plus(duration);
                previousEnd = endDateTime;
                setEndime(endDateTime.toLocalTime());
                endDateLabel.setText(endDateTime.toLocalDate().format(DATE_FORMATTER));
            } finally {
                isUpdating = false;
            }
        }
    }

    private LocalTime getStartTime() {
        int hour = cbHourStart.getValue();
        int minute = cbMinuteStart.getValue();
        return LocalTime.of(hour, minute);
    }

    private Duration getDuration() {
        int hour = cbHourDuration.getValue();
        int minute = cbMinuteDuration.getValue();
        return Duration.ofHours(hour).plusMinutes(minute);
    }
    
    private LocalTime getEndTime() {
        int hour = cbHourEnd.getValue();
        int minute = cbMinuteEnd.getValue();
        return LocalTime.of(hour, minute);
    }
    
    private void setStartTime(LocalTime time) {
        cbHourStart.getValueFactory().setValue(time.getHour());
        cbMinuteStart.getValueFactory().setValue(time.getMinute());
    }

    private void setDuration(Duration time) {
        cbHourDuration.getValueFactory().setValue(((int) time.toHours()));
        cbMinuteDuration.getValueFactory().setValue(time.toMinutesPart());
    }

    private void setEndime(LocalTime time) {
        cbHourEnd.getValueFactory().setValue(time.getHour());
        cbMinuteEnd.getValueFactory().setValue(time.getMinute());
    }

    public void onActionProcess() {
        Duration duration = getDuration();

        Activity activity = Activity.builder()
                .type(activityTypeChoiceBox.getValue())
                .startTime(LocalDateTime.of(myDate,getStartTime()))
                .duration(duration)
                .cardStatus(cardInserted.isSelected() ? "inserted" : "notInserted")
                .build();

        if(mainController.driverInterface.getBlocks().isEmpty()){
            mainController.driverInterface.addBlock(activity);
        } else {
            mainController.driverInterface.addBlock(insertionIndex, activity);
        }
        
        Stage stage = (Stage) processButton.getScene().getWindow();
        stage.close();
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
