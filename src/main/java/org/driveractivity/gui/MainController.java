package org.driveractivity.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Setter;
import org.driveractivity.entity.Activity;
import org.driveractivity.entity.ActivityType;
import org.driveractivity.exception.FileImportException;
import org.driveractivity.service.DriverInterface;
import org.driveractivity.service.DriverService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static org.driveractivity.entity.ActivityType.*;

public class MainController implements Initializable {
    @FXML
    public ActivityPane activityPane;
    @FXML
    public Button clearButton;

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
        activityPane.setMainController(this);
        driverInterface = DriverService.getInstance();
        SampleData.populate(driverInterface, 40);
        activityPane.initialize(driverInterface);
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
        int insertionIndex = activityPane.getSelectedBlock().map(integer -> integer + 1).orElse(driverInterface.getBlocks().size());
        this.openDateHandlerStage(type, insertionIndex, null);
    }

    @FXML
    private void clearActivities(ActionEvent event) {
        driverInterface.clear();
    }

    public void openDateHandlerStage(ActivityType currentActivityType, int insertionIndex, Activity editActivity) {
        try {
            FXMLLoader loader = new FXMLLoader(DateHandler.class.getResource("DataHandler.fxml"));
            Parent root = loader.load();
            DateHandler controller = loader.getController();
            controller.initialize(this, insertionIndex, currentActivityType, editActivity);
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initStyle(StageStyle.UTILITY);
            dialogStage.setScene(new Scene(root, 380, 250));
            dialogStage.setTitle("Setting up " + currentActivityType + "...");
            dialogStage.toFront();
            dialogStage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
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

        try {
            driverInterface.importFrom(file);
        } catch (FileImportException e) {
            AlertedExceptionDialog.show(e);
        }
    }

    @FXML
    private void saveFile(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save XML-File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File file = fileChooser.showSaveDialog(stage);

        driverInterface.exportToXML(file);
    }

    @FXML
    private void toggleDayDivider(ActionEvent event){
        ToggleButton toggleButton = (ToggleButton) event.getSource();
        activityPane.setRenderDayDividers(toggleButton.isSelected());
        updateToggleButton(toggleButton);
    }

    @FXML
    public void toggleWeekDivider(ActionEvent event) {
        ToggleButton toggleButton = (ToggleButton) event.getSource();
        activityPane.setRenderWeekDividers(toggleButton.isSelected());
        updateToggleButton(toggleButton);
    }

    @FXML
    public void toggleCardStatus(ActionEvent event) {
        ToggleButton toggleButton = (ToggleButton) event.getSource();
        activityPane.setRenderCardStatus(toggleButton.isSelected());
        updateToggleButton(toggleButton);
    }

    @FXML
    public void toggleSpecificConditions(ActionEvent event) {
        ToggleButton toggleButton = (ToggleButton) event.getSource();
        activityPane.setRenderSpecificConditions(toggleButton.isSelected());
        updateToggleButton(toggleButton);
    }

    private void updateToggleButton(ToggleButton toggleButton) {
        boolean selected = toggleButton.isSelected();
        toggleButton.setText(selected ? "ON" : "OFF");
    }
}