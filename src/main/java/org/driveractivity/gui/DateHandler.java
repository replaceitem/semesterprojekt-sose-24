package org.driveractivity.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.stage.StageStyle;
import org.driveractivity.entity.Activity;
import org.driveractivity.entity.ActivityType;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

public class DateHandler implements Initializable {

    private final MainController mainController;
    private final ActivityType currentActivityType;
    
    @FXML
    private Label errorLabel;

    private Activity activity;
    @FXML
    private Stage dialogStage;
    @FXML
    private GridPane myGridPane;
    @FXML
    private ComboBox<String> cbHourStart;
    @FXML
    private ComboBox<String> cbHourEnd;
    @FXML
    private ComboBox<String> cbHourDuration;
    @FXML
    private ComboBox<String> cbMinuteStart;
    @FXML
    private ComboBox<String> cbMinuteEnd;
    @FXML
    private ComboBox<String> cbMinuteDuration;
    @FXML
    private CheckBox activeDuration;
    @FXML
    private CheckBox activeEnd;
    @FXML
    private CheckBox activeStart;
    @FXML
    private Button processButton;

    public DateHandler(MainController mainController, ActivityType currentActivityType) {
        this.mainController = mainController;
        this.currentActivityType = currentActivityType;
    }

    public void openDateHandlerStage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DataHandler.fxml"));
            //    org/driveractivity/gui/DataHandler.fxml
            Parent root = loader.load();
            // Erhalte die aktuelle Instanz, um auf FXML-Elemente zuzugreifen
            DateHandler controller = loader.getController();
            dialogStage = new Stage();

            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initStyle(StageStyle.UTILITY);
            // Scene und Titel festlegen
            dialogStage.setScene(new Scene(root, 300, 200));
            dialogStage.setTitle("Setting up "+ currentActivityType +"...");
            // Zeige das neue Fenster und setze es in den Vordergrund
            dialogStage.toFront();

            //dialogStage.show();
            dialogStage.showAndWait();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private ComboBox<String> cloneComboBox(ComboBox<String> original) {
        // Erstellen einer neuen ComboBox und Kopieren von Eigenschaften
        ComboBox<String> newComboBox = new ComboBox<>();
        newComboBox.getItems().addAll(original.getItems()); // Kopieren der Elemente
        newComboBox.setEditable(original.isEditable()); // Editierbarkeit kopieren
        newComboBox.getSelectionModel().select(original.getSelectionModel().getSelectedIndex()); // Auswahl kopieren
        return newComboBox;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorLabel.setVisible(false);
        for (int i = 0; i <= 23; i++) {
            String hours;
            if (i < 10) {
                hours = "0" + i;
            }
            hours = String.valueOf(i);
            cbHourDuration.getItems().add(hours);
            cbHourStart.getItems().add(hours);
            cbHourEnd.getItems().add(hours);
        }


        for (int i = 0; i <= 59; i++) {
            String minutes;
            if(i<10){
                minutes = "0"+i;
            }
            minutes = String.valueOf(i);

            cbMinuteStart.getItems().add(minutes);
            cbMinuteEnd.getItems().add(minutes);
            cbMinuteDuration.getItems().add(minutes);
        }

    }
    public void onHandleStart(ActionEvent actionEvent) {
        checkForValidBoxes();
        if(activeStart.isSelected()&& activeEnd.isSelected()) {
            processStartEnd();
        }else if(activeStart.isSelected()&& activeDuration.isSelected()){
            durationWithStart();
        }
    }
    public void onHandleEnd(ActionEvent actionEvent) {
        checkForValidBoxes();
        if(activeEnd.isSelected()&& activeStart.isSelected()) {
            processStartEnd();
        }else if(activeEnd.isSelected()&& activeDuration.isSelected()){
            durationWithEnd();
        }

    }

    private boolean checkForValidDuration() {
        boolean t = (cbHourDuration.getValue() != null && cbMinuteDuration.getValue() != null);
        return (cbHourStart.getValue() != null && cbMinuteStart.getValue() != null && t) ||
                (cbHourEnd.getValue() != null && cbMinuteEnd.getValue() != null) && t;
    }

    private void checkForValidBoxes(){
        if(activeStart.isSelected()&& activeEnd.isSelected()&& activeDuration.isSelected()){
            showError("Alle drei sind Aktiv bitte nur 2 Aktiv haben!");
            //JOptionPane.showMessageDialog (null, "Alle drei sind Aktiv bitte nur 2 Aktiv haben!", "Something went Wrong", JOptionPane.ERROR_MESSAGE);
        }else {
            showError("");
        }
    }
    private void processStartEnd(){
        if (cbHourStart.getValue() != null && cbMinuteStart.getValue() != null ) {
            if (cbHourEnd.getValue() != null && cbMinuteEnd.getValue() != null ) {
                if (Integer.parseInt(cbHourStart.getValue()) <= Integer.parseInt(cbHourEnd.getValue())) {
                    if (Integer.parseInt(cbMinuteStart.getValue()) <= Integer.parseInt(cbMinuteEnd.getValue())) {

                        showError("");
                        processButton.setDisable(false);
                        errorLabel.setVisible(false);
                        cbHourDuration.setValue(String.valueOf(Integer.parseInt(cbHourEnd.getValue()) - Integer.parseInt(cbHourStart.getValue())));
                        cbMinuteDuration.setValue(String.valueOf(Integer.parseInt(cbMinuteEnd.getValue()) - Integer.parseInt(cbMinuteStart.getValue())));

                    } else {
                        showError("Start Zeit größer oder gleich Endzeit");
                        //a.showMessageDialog(null, "Start Zeit größer oder gleich Endzeit", "Something went Wrong", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    showError("Start Zeit größer oder gleich Endzeit");
                    //a.showMessageDialog(null, "Start Zeit größer oder gleich Endzeit", "Something went Wrong", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }

    public void onActionProcess() {
        int hour = Integer.parseInt(cbHourDuration.getValue());
        int minute = Integer.parseInt(cbMinuteDuration.getValue());

        int duration = hour*60 + minute;

        hour = Integer.parseInt(cbHourStart.getValue());
        minute = Integer.parseInt(cbMinuteStart.getValue());

        LocalDateTime startTime = LocalDateTime.now()
                .withHour(hour)
                .withMinute(minute);

        activity = new Activity(currentActivityType, Duration.of(duration, ChronoUnit.MINUTES), startTime);

        mainController.activityPane.addBack(activity);

        Stage stage = (Stage) processButton.getScene().getWindow();
        stage.close();
    }

    public void onActionActiveStart(ActionEvent actionEvent) {
        checkForValidBoxes();
        if(activeStart.isSelected()){
            cbHourStart.setDisable(false);
            cbMinuteStart.setDisable(false);
            cbHourStart.setValue(null);
            cbMinuteStart.setValue(null);
        }else {
            cbHourStart.setDisable(true);
            cbHourStart.setValue(null);
            cbMinuteStart.setDisable(true);
            cbMinuteStart.setValue(null);
        }
    }

    public void onActionActiveEnd(ActionEvent actionEvent) {
        //noinspection DuplicatedCode
        checkForValidBoxes();
        if(activeEnd.isSelected()){
            cbHourEnd.setValue(null);
            cbMinuteEnd.setValue(null);
            cbHourEnd.setDisable(false);
            cbMinuteEnd.setDisable(false);
        }else {
            cbHourEnd.setDisable(true);
            cbMinuteEnd.setDisable(true);
            cbHourEnd.setValue(null);
            cbMinuteEnd.setValue(null);
        }
    }

    public void onActionActiveDuration(ActionEvent actionEvent) {
        //noinspection DuplicatedCode
        checkForValidBoxes();
        if(activeDuration.isSelected()){
            cbHourDuration.setValue(null);
            cbMinuteDuration.setValue(null);
            cbHourDuration.setDisable(false);
            cbMinuteDuration.setDisable(false);
        }else {
            cbHourDuration.setDisable(true);
            cbMinuteDuration.setDisable(true);
            cbHourDuration.setValue(null);
            cbMinuteDuration.setValue(null);
        }
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

    public void onHandleDuration(ActionEvent actionEvent) {
        if(activeDuration.isSelected()&& activeStart.isSelected()){
            durationWithStart();
        }
        else if((activeDuration.isSelected()&& activeEnd.isSelected())){
            durationWithEnd();
        }
    }
    private void durationWithStart(){
        if(checkForValidDuration()) {
            cbHourEnd.setValue(String.valueOf(Integer.parseInt(cbHourDuration.getValue()) + Integer.parseInt(cbHourStart.getValue())));
            cbMinuteEnd.setValue(String.valueOf(Integer.parseInt(cbMinuteDuration.getValue()) + Integer.parseInt(cbMinuteStart.getValue())));
        }
    }
    private void durationWithEnd(){
        if(checkForValidDuration()){
            cbHourStart.setValue(String.valueOf(Integer.parseInt(cbHourEnd.getValue()) - Integer.parseInt(cbHourDuration.getValue())));
            cbMinuteStart.setValue(String.valueOf(Integer.parseInt(cbMinuteEnd.getValue()) - Integer.parseInt(cbMinuteDuration.getValue())));
        }
    }
}
