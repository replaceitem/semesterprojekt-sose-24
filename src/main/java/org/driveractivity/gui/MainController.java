package org.driveractivity.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Setter;
import org.driveractivity.entity.Activity;
import org.driveractivity.entity.ActivityType;

import java.io.File;
import java.net.URL;
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

    @FXML
    private MenuItem openMenu;

    public static List<Activity> activities;
    public static ActivityType currentActivityType;

    @Setter
    private Stage stage;

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
            activityPane.addBack(activities.getLast());
        }
        else if (button == driveButton) {
            currentActivityType = DRIVING;
            dh.openDateHandlerStage();
            System.out.println("driveButton");
            activityPane.addBack(activities.getLast());
        }
        else if (button == workButton) {
            currentActivityType = WORK;
            dh.openDateHandlerStage();
            System.out.println("workButton");
            activityPane.addBack(activities.getLast());
        }
        else if (button == availableButton) {
            currentActivityType = AVAILABLE;
            dh.openDateHandlerStage();
            System.out.println("availableButton");
            activityPane.addBack(activities.getLast());
        }
        else{
            System.out.println("unknown button");
        }
    }

    @FXML
    private void openFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open XML-File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File file = fileChooser.showOpenDialog(stage);
    }

    @FXML
    private void saveFile(ActionEvent event) {
            //ActivitiesList to XML, save to File
    }

}