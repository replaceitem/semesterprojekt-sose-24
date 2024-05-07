package org.driveractivity.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import org.driveractivity.entity.Activity;

import java.net.URL;
import java.util.ResourceBundle;

import static org.driveractivity.entity.ActivityType.*;

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
        DateHandler dh = new DateHandler();
        if (button == restButton) {
            dh.setActivityType(REST);
            Activity a = dh.openDateHandlerStage();
            System.out.println("restButton");
        }
        else if (button == driveButton) {
            dh.openDateHandlerStage();
            System.out.println("driveButton");
        }
        else if (button == workButton) {
            dh.openDateHandlerStage();
            System.out.println("workButton");
        }
        else if (button == availableButton) {
            dh.openDateHandlerStage();
            System.out.println("availableButton");
        }
        else{
            System.out.println("unknown button");
        }
    }
}