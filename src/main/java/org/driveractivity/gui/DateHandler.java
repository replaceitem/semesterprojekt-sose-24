package org.example.demo1;

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

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DateHandler extends Component implements Initializable {

    public Label errorLable;
    private String acktivity;
    private Stage dialogStage;
    public GridPane myGridPane;
    public ComboBox cb_Hour_start;
    public ComboBox cb_Hour_end;
    public ComboBox cb_Hour_duration;
    public ComboBox cb_minute_start;
    public ComboBox cb_minute_end;
    public ComboBox cb_minute_duration;
    public ComboBox cb_second_start;
    public ComboBox cb_second_end;
    public ComboBox cb_second_duration;
    public CheckBox aktiv_duration;
    public CheckBox aktiv_end;
    public CheckBox aktiv_start;
    @FXML
    private Button processButton;



    public String openDateHandlerStage(Stage ownerStage,String type) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo1/DataHandler.fxml"));
            Parent root = loader.load();
            // Erhalte die aktuelle Instanz, um auf FXML-Elemente zuzugreifen
            DateHandler controller = loader.getController();
            dialogStage = new Stage();
            dialogStage.initOwner(ownerStage);

            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initStyle(StageStyle.UTILITY);
            // Scene und Titel festlegen
            dialogStage.setScene(new Scene(root, 300, 200));
            dialogStage.setTitle("Setting up "+ type +"...");

            // Zeige das neue Fenster und setze es in den Vordergrund
            dialogStage.toFront();
            //dialogStage.show();
            dialogStage.showAndWait();
            return acktivity;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
        errorLable.setVisible(false);
        for (int i = 0; i <= 23; i++) {
            String Iteam;
            if (i < 10) {
                Iteam = "0" + i;
            }
            Iteam = String.valueOf(i);
            cb_Hour_duration.getItems().add(Iteam);
            cb_Hour_start.getItems().add(Iteam);
            cb_Hour_end.getItems().add(Iteam);
        }


        for (int i = 0; i <= 59; i++) {
            String Iteam;
            if(i<10){
                Iteam = "0"+i;
            }
            Iteam = String.valueOf(i);

            cb_minute_start.getItems().add(Iteam);
            cb_minute_end.getItems().add(Iteam);
            cb_minute_duration.getItems().add(Iteam);

            cb_second_start.getItems().add(Iteam);
            cb_second_end.getItems().add(Iteam);
            cb_second_duration.getItems().add(Iteam);
        }

    }
    public void onHandleStart(ActionEvent actionEvent) {
        checkforValidBoxes();
        if(aktiv_start.isSelected()&&aktiv_end.isSelected()) {
            proccesStartEnd();
        }else if(aktiv_start.isSelected()&&aktiv_duration.isSelected()){
            durationwithStart();
        }
    }
    public void onHandleEnd(ActionEvent actionEvent) {
        checkforValidBoxes();
        if(aktiv_end.isSelected()&&aktiv_start.isSelected()) {
            proccesStartEnd();
        }else if(aktiv_end.isSelected()&&aktiv_duration.isSelected()){
            durationwithEnd();
        }

    }

    private boolean checkforValidDuration() {
        boolean t = (cb_Hour_duration.getValue() != null && cb_minute_duration.getValue() != null && cb_second_duration.getValue() != null);
        if((cb_Hour_start.getValue() != null && cb_minute_start.getValue() != null && cb_second_start.getValue() != null&&t)||
                (cb_Hour_end.getValue() != null && cb_minute_end.getValue() != null && cb_second_end.getValue() != null)&&t)
        {
            return true;
        }
        return false;
    }

    private void checkforValidBoxes(){
        if(aktiv_start.isSelected()&&aktiv_end.isSelected()&&aktiv_duration.isSelected()){
            showError("Alle drei sind Aktiv bitte nur 2 Aktiv haben!");
            //JOptionPane.showMessageDialog (null, "Alle drei sind Aktiv bitte nur 2 Aktiv haben!", "Something went Wrong", JOptionPane.ERROR_MESSAGE);
        }else {
            showError("");
        }
    }
    private void proccesStartEnd(){
        if (cb_Hour_start.getValue() != null && cb_minute_start.getValue() != null && cb_second_start.getValue() != null) {
            if (cb_Hour_end.getValue() != null && cb_minute_end.getValue() != null && cb_second_end.getValue() != null) {
                if (Integer.parseInt(cb_Hour_start.getValue().toString()) <= Integer.parseInt(cb_Hour_end.getValue().toString())) {
                    if (Integer.parseInt(cb_minute_start.getValue().toString()) <= Integer.parseInt(cb_minute_end.getValue().toString())) {
                        if (Integer.parseInt(cb_second_start.getValue().toString()) <= Integer.parseInt(cb_second_end.getValue().toString())) {
                            showError("");
                            processButton.setDisable(false);
                            errorLable.setVisible(false);
                            cb_Hour_duration.setValue(Integer.valueOf(cb_Hour_end.getValue().toString()) - Integer.valueOf(cb_Hour_start.getValue().toString()));
                            cb_minute_duration.setValue(Integer.valueOf(cb_minute_end.getValue().toString()) - Integer.valueOf(cb_minute_start.getValue().toString()));
                            cb_second_duration.setValue(Integer.valueOf(cb_second_end.getValue().toString()) - Integer.valueOf(cb_second_start.getValue().toString()));
                        } else {

                            showError("Start Zeit größer oder gleich Endzeit");
                            //a.showMessageDialog(null, "Start Zeit größer oder gleich Endzeit", "Something went Wrong", JOptionPane.WARNING_MESSAGE);
                        }
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

    public void processButtonAonAction(ActionEvent actionEvent) {
        Stage stage = (Stage) processButton.getScene().getWindow();
        stage.close();
    }

    public void onAction_aktiv_start(ActionEvent actionEvent) {
        checkforValidBoxes();
        if(aktiv_start.isSelected()){
            cb_Hour_start.setDisable(false);
            cb_minute_start.setDisable(false);
            cb_second_start.setDisable(false);
            cb_Hour_start.setValue(null);
            cb_minute_start.setValue(null);
            cb_second_start.setValue(null);
        }else {
            cb_Hour_start.setDisable(true);
            cb_Hour_start.setValue(null);
            cb_minute_start.setDisable(true);
            cb_minute_start.setValue(null);
            cb_second_start.setDisable(true);
            cb_second_start.setValue(null);
        }
    }

    public void onAction_aktiv_end(ActionEvent actionEvent) {
        checkforValidBoxes();
        if(aktiv_end.isSelected()){
            cb_Hour_end.setValue(null);
            cb_minute_end.setValue(null);
            cb_second_end.setValue(null);
            cb_Hour_end.setDisable(false);
            cb_minute_end.setDisable(false);
            cb_second_end.setDisable(false);
        }else {
            cb_Hour_end.setDisable(true);
            cb_minute_end.setDisable(true);
            cb_second_end.setDisable(true);
            cb_Hour_end.setValue(null);
            cb_minute_end.setValue(null);
            cb_second_end.setValue(null);
        }
    }

    public void onAction_aktiv_duration(ActionEvent actionEvent) {
        checkforValidBoxes();
        if(aktiv_duration.isSelected()){
            cb_Hour_duration.setValue(null);
            cb_minute_duration.setValue(null);
            cb_second_duration.setValue(null);
            cb_Hour_duration.setDisable(false);
            cb_minute_duration.setDisable(false);
            cb_second_duration.setDisable(false);
        }else {
            cb_Hour_duration.setDisable(true);
            cb_minute_duration.setDisable(true);
            cb_second_duration.setDisable(true);
            cb_Hour_duration.setValue(null);
            cb_minute_duration.setValue(null);
            cb_second_duration.setValue(null);
        }
    }
    private void showError(String error){
        if(error.equals("")){
            errorLable.setVisible(false);
            processButton.setDisable(false);
        }else {
            errorLable.setVisible(true);
            processButton.setDisable(true);
            errorLable.setText(error);
        }
    }

    public void onHandleDuration(ActionEvent actionEvent) {
        if(aktiv_duration.isSelected()&&aktiv_start.isSelected()){
            durationwithStart();
        }
        else if((aktiv_duration.isSelected()&&aktiv_end.isSelected())){
            durationwithEnd();
        }
    }
    private void durationwithStart(){
        if(checkforValidDuration()) {
            cb_Hour_end.setValue(Integer.valueOf(cb_Hour_duration.getValue().toString()) + Integer.valueOf(cb_Hour_start.getValue().toString()));
            cb_minute_end.setValue(Integer.valueOf(cb_minute_duration.getValue().toString()) + Integer.valueOf(cb_minute_start.getValue().toString()));
            cb_second_end.setValue(Integer.valueOf(cb_second_duration.getValue().toString()) + Integer.valueOf(cb_second_start.getValue().toString()));
        }
    }
    private void durationwithEnd(){
        if(checkforValidDuration()){
            cb_Hour_start.setValue(Integer.valueOf(cb_Hour_end.getValue().toString()) - Integer.valueOf(cb_Hour_duration.getValue().toString()));
            cb_minute_start.setValue(Integer.valueOf(cb_minute_end.getValue().toString()) - Integer.valueOf(cb_minute_duration.getValue().toString()));
            cb_second_start.setValue(Integer.valueOf(cb_second_end.getValue().toString()) - Integer.valueOf(cb_second_duration.getValue().toString()));
        }
    }
}
