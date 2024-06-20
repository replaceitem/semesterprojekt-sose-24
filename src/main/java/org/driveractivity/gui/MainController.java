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
import java.io.FileOutputStream;
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

    @FXML
    private ToggleButton dayToggle;
    @FXML
    private ToggleButton weekToggle;
    @FXML
    private ToggleButton cardToggle;
    @FXML
    private ToggleButton conditionToggle;


    public DriverInterface driverInterface;
    @Setter
    private Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        activityPane.setMainController(this);
        driverInterface = DriverService.getInstance();
        activityPane.initialize(driverInterface);

        dayToggle.setSelected(Boolean.parseBoolean(MainApplication.appProperties.getProperty("renderDayDividers")));
        weekToggle.setSelected(Boolean.parseBoolean(MainApplication.appProperties.getProperty("renderWeekDividers")));
        cardToggle.setSelected(Boolean.parseBoolean(MainApplication.appProperties.getProperty("renderCardStatus")));
        conditionToggle.setSelected(Boolean.parseBoolean(MainApplication.appProperties.getProperty("renderSpecificConditions")));

        updateToggleButton(dayToggle);
        updateToggleButton(weekToggle);
        updateToggleButton(cardToggle);
        updateToggleButton(conditionToggle);

    }

    @FXML
    private void addActivity(ActionEvent event) {
        Button button = (Button) event.getSource();
        ActivityType type = null;
        if (button == restButton) type = REST;
        else if (button == driveButton) type = DRIVING;
        else if (button == workButton) type = WORK;
        else if (button == availableButton) type = AVAILABLE;
        else return;
        int insertionIndex = activityPane.getSelectedBlock().map(integer -> integer + 1).orElse(driverInterface.getBlocks().size());
        this.openActivityEditorStage(type, insertionIndex, null);
    }

    @FXML
    private void clearActivities(ActionEvent event) {
        driverInterface.clear();
    }

    public void openActivityEditorStage(ActivityType currentActivityType, int insertionIndex, Activity editActivity) {
        try {
            FXMLLoader loader = new FXMLLoader(ActivityEditor.class.getResource("activity-editor.fxml"));
            Parent root = loader.load();
            ActivityEditor controller = loader.getController();
            controller.initialize(this, insertionIndex, currentActivityType, editActivity);
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initStyle(StageStyle.UTILITY);
            dialogStage.setScene(new Scene(root, 380, 400));
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
        fileChooser.setInitialDirectory(new File(MainApplication.appProperties.getProperty("openFilePath")));

        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try{
                MainApplication.appProperties.setProperty("openFilePath", file.getParentFile().getAbsolutePath());
                MainApplication.appProperties.store(new FileOutputStream(System.getProperty("user.home") + "/DriverTestApp/app.properties"), "New OpenFile Path");
            }catch (IOException e){
                e.printStackTrace();
            }

            try {
                driverInterface.importFrom(file);
            } catch (FileImportException e) {
                AlertedExceptionDialog.show(e);
            }
        }

    }

    @FXML
    private void saveFile(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save XML-File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        fileChooser.setInitialDirectory(new File(MainApplication.appProperties.getProperty("saveFilePath")));

        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                MainApplication.appProperties.setProperty("saveFilePath", file.getParentFile().getAbsolutePath());
                MainApplication.appProperties.store(new FileOutputStream(System.getProperty("user.home") + "/DriverTestApp/app.properties"), "New SaveFile Path");
            } catch (IOException e) {
                e.printStackTrace();
            }

            driverInterface.exportToXML(file);
        }
    }

    @FXML
    private void toggleDayDivider(ActionEvent event){
        ToggleButton toggleButton = (ToggleButton) event.getSource();
        activityPane.setRenderDayDividers(toggleButton.isSelected());
        updateToggleButton(toggleButton);

        try{
            MainApplication.appProperties.setProperty("renderDayDividers", String.valueOf(toggleButton.isSelected()));
            MainApplication.appProperties.store(new FileOutputStream(System.getProperty("user.home") + "/DriverTestApp/app.properties"), "New SaveFile Path");
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void toggleWeekDivider(ActionEvent event) {
        ToggleButton toggleButton = (ToggleButton) event.getSource();
        activityPane.setRenderWeekDividers(toggleButton.isSelected());
        updateToggleButton(toggleButton);

        try{
            MainApplication.appProperties.setProperty("renderWeekDividers", String.valueOf(toggleButton.isSelected()));
            MainApplication.appProperties.store(new FileOutputStream(System.getProperty("user.home") + "/DriverTestApp/app.properties"), "New SaveFile Path");
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void toggleCardStatus(ActionEvent event) {
        ToggleButton toggleButton = (ToggleButton) event.getSource();
        activityPane.setRenderCardStatus(toggleButton.isSelected());
        updateToggleButton(toggleButton);

        try{
            MainApplication.appProperties.setProperty("renderCardStatus", String.valueOf(toggleButton.isSelected()));
            MainApplication.appProperties.store(new FileOutputStream(System.getProperty("user.home") + "/DriverTestApp/app.properties"), "New SaveFile Path");
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void toggleSpecificConditions(ActionEvent event) {
        ToggleButton toggleButton = (ToggleButton) event.getSource();
        activityPane.setRenderSpecificConditions(toggleButton.isSelected());
        updateToggleButton(toggleButton);

        try{
            MainApplication.appProperties.setProperty("renderSpecificConditions", String.valueOf(toggleButton.isSelected()));
            MainApplication.appProperties.store(new FileOutputStream(System.getProperty("user.home") + "/DriverTestApp/app.properties"), "New SaveFile Path");
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void updateToggleButton(ToggleButton toggleButton) {
        boolean selected = toggleButton.isSelected();
        toggleButton.setText(selected ? "ON" : "OFF");
    }

    public void onMoveForward(ActionEvent actionEvent) {
        moveActivity(1);
    }

    public void onMoveBackward(ActionEvent actionEvent) {
        moveActivity(-1);
    }
    
    private void moveActivity(int shift) {
        activityPane.getSelectedBlock().ifPresent(index -> {
            int newIndex = index+shift;
            int maxIndex = driverInterface.getBlocks().size() - 1;
            if(index < 0 || index > maxIndex) return;
            if(newIndex < 0 || newIndex > maxIndex) return;
            Activity activity = driverInterface.getBlocks().get(index);
            driverInterface.removeBlock(index);
            driverInterface.addBlock(newIndex, activity);
            activityPane.setSelectedBlock(newIndex);
        });
    }
}