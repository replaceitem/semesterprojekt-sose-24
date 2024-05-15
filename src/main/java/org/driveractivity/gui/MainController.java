package org.driveractivity.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import org.driveractivity.entity.ActivityType;
import org.driveractivity.service.DriverInterface;
import org.driveractivity.service.DriverService;

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

    public DriverInterface driverInterface;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        driverInterface = new DriverService();
        SampleData.populate(driverInterface, 40);
        activityPane.load(driverInterface);
    }

    @FXML
    private void addActivity(ActionEvent event) {
        Button button = (Button) event.getSource();
        ActivityType type = null;
        if (button == restButton) type = REST;
        else if (button == driveButton) type = DRIVING;
        else if (button == workButton) type = WORK;
        else if (button == availableButton) type = AVAILABLE;
        else System.out.println("unknown button");
        
        DateHandler dh = new DateHandler(this, type);
        dh.openDateHandlerStage();
    }
}