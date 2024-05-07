package org.driveractivity.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import org.driveractivity.entity.Activity;
import org.driveractivity.entity.ActivityType;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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

    public static List<Activity> activities;
    public static ActivityType currentActivityType;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        activities = SampleData.getSampleData(40);
        activityPane.load(activities);
    }

    @FXML
    private void addActivity(ActionEvent event) {
        Button button = (Button) event.getSource();
        DateHandler dh = new DateHandler();
        if (button == restButton) {
            currentActivityType = REST;
            dh.openDateHandlerStage();
            System.out.println("restButton");
            activityPane.load(activities.getLast());
        }
        else if (button == driveButton) {
            currentActivityType = DRIVING;
            dh.openDateHandlerStage();
            System.out.println("driveButton");
            activityPane.load(activities.getLast());
        }
        else if (button == workButton) {
            currentActivityType = WORK;
            dh.openDateHandlerStage();
            System.out.println("workButton");
            activityPane.load(activities.getLast());
        }
        else if (button == availableButton) {
            currentActivityType = AVAILABLE;
            dh.openDateHandlerStage();
            System.out.println("availableButton");
            activityPane.load(activities.getLast());
        }
        else{
            System.out.println("unknown button");
        }
    }
}