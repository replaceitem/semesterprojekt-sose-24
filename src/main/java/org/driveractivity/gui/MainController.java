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
import org.driveractivity.service.DriverInterface;
import org.driveractivity.service.DriverService;

import java.io.File;
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

    @FXML
    private MenuItem openMenu;

    public DriverInterface driverInterface;

    @Setter
    private Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        driverInterface = DriverService.getInstance();
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

        DateHandler.openDateHandlerStage(this, type);
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