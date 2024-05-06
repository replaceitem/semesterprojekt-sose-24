package org.driveractivity.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    public ActivityPane activityPane;

    @FXML
    private Button restButton;
    @FXML
    private Button driveButton;
    @FXML
    private Button workButton;
    @FXML
    private Button availableButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        activityPane.load(SampleData.getSampleData(40));
    }

    @FXML
    private void addActivity(ActionEvent event) {
        Button button = (Button) event.getSource();

        if (button == restButton) {
            // open dialog
            System.out.println("restButton");
        }
        else if (button == driveButton) {
            // open dialog
            System.out.println("driveButton");
        }
        else if (button == workButton) {
            // open dialog
            System.out.println("workButton");
        }
        else if (button == availableButton) {
            // open dialog
            System.out.println("availableButton");
        }
        else{
            System.out.println("unknown button");
        }
    }
}