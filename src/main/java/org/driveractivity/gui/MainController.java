package org.driveractivity.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Setter;
import org.driveractivity.entity.*;
import org.driveractivity.exception.*;
import org.driveractivity.service.DriverInterface;
import org.driveractivity.service.DriverService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.*;
import java.util.*;

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
    public VBox specificConditions;

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
        ActivityType type;
        if (button == restButton) type = REST;
        else if (button == driveButton) type = DRIVING;
        else if (button == workButton) type = WORK;
        else if (button == availableButton) type = AVAILABLE;
        else return;
        int insertionIndex = activityPane.getSelectedBlock().map(integer -> integer + 1).orElse(driverInterface.getBlocks().size());
        this.openActivityEditorStage(type, insertionIndex, null);
    }

    @FXML
    private void clearActivities() {
        driverInterface.clear();
    }

    public void openActivityEditorStage(ActivityType currentActivityType, int insertionIndex, Activity editActivity) {
        try {
            FXMLLoader loader = new FXMLLoader(ActivityEditor.class.getResource("activity-editor.fxml"));
            Parent root = loader.load();
            ActivityEditor controller = loader.getController();
            controller.initialize(this, insertionIndex, currentActivityType, editActivity);
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.DECORATED);
            dialogStage.setResizable(false);
            dialogStage.getIcons().addAll(Icons.APP_ICONS);
            dialogStage.setScene(new Scene(root, 380, 400));
            dialogStage.setTitle("Setting up " + currentActivityType + "...");
            dialogStage.toFront();
            dialogStage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void openFile() {
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
                loadSpecificConditions();
            } catch (FileImportException e) {
                AlertedExceptionDialog.show(e);
            }
        }

    }

    @FXML
    private void saveFile() {
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

            try {
                driverInterface.exportToXML(file);
            } catch (FileExportException e) {
                AlertedExceptionDialog.show(e);
            }
        }
    }

    @FXML
    public void openAbout() {
        try {
            FXMLLoader loader = new FXMLLoader(ActivityEditor.class.getResource("about.fxml"));
            Parent root = loader.load();
            Stage aboutStage = new Stage(StageStyle.DECORATED);
            aboutStage.initModality(Modality.APPLICATION_MODAL);
            aboutStage.setResizable(false);
            aboutStage.getIcons().addAll(Icons.APP_ICONS);
            aboutStage.setScene(new Scene(root, 500, 400));
            aboutStage.setTitle("About");
            aboutStage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    public void onMoveForward() {
        moveActivity(1);
    }

    public void onMoveBackward() {
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
    
    public void loadSpecificConditions() {
        specificConditions.getChildren().setAll(driverInterface.getSpecificConditions().stream().map(specificCondition -> new SpecificConditionEntry(this, specificCondition)).toList());
    }

    public void onAddBeginFerryTrain() {
        addFerryTrain(SpecificConditionType.BEGIN_FT);
    }

    public void onAddEndFerryTrain() {
        addFerryTrain(SpecificConditionType.END_FT);
    }

    public void onAddOutOfScope() {
        Optional<LocalDateTime> beginOutOfScopeTime = openDateTimePicker("beginOutOfScope time", "Choose a time for beginOutOfScope specific condition");
        if(beginOutOfScopeTime.isEmpty()) return;
        Optional<LocalDateTime> endOutOfScopeTime = openDateTimePicker("endOutOfScopeTime time", "Choose a time for endOutOfScopeTime specific condition");
        if(endOutOfScopeTime.isEmpty()) return;
        SpecificCondition beginCondition = SpecificCondition.builder()
                .timestamp(beginOutOfScopeTime.get())
                .specificConditionType(SpecificConditionType.BEGIN_OUT_OF_SCOPE)
                .build();
        SpecificCondition endCondition = SpecificCondition.builder()
                .timestamp(endOutOfScopeTime.get())
                .specificConditionType(SpecificConditionType.END_OUT_OF_SCOPE)
                .build();

        try {
            driverInterface.addSpecificCondition(List.of(beginCondition, endCondition));
        } catch (SpecificConditionException e) {
            AlertedExceptionDialog.show(e);
        }
        loadSpecificConditions();
    }
    
    public void addFerryTrain(SpecificConditionType specificConditionType) {
        String name = specificConditionType.mapNameToString();
        Optional<LocalDateTime> ferryTrainStartTime = openDateTimePicker(name + " time", "Choose a time for " + name + " specific condition");
        if(ferryTrainStartTime.isEmpty()) return;
        SpecificCondition specificCondition = SpecificCondition.builder()
                .timestamp(ferryTrainStartTime.get())
                .specificConditionType(specificConditionType)
                .build();
        try {
            driverInterface.addSpecificCondition(List.of(specificCondition));
        } catch (SpecificConditionException e) {
            AlertedExceptionDialog.show(e);
        }
        loadSpecificConditions();
    }
    
    
    
    public Optional<LocalDateTime> openDateTimePicker(String title, String header) {
        Dialog<LocalDateTime> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        VBox vbox = new VBox();

        Spinner<Integer> hourSpinner = new Spinner<>();
        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
        Spinner<Integer> minuteSpinner = new Spinner<>();
        minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));

        DatePicker datePicker = new DatePicker(LocalDate.now());
        
        vbox.getChildren().addAll(
                new Label("Time:"),
                new HBox(hourSpinner, minuteSpinner),
                new Label("Date:"),
                datePicker
        );

        dialog.getDialogPane().setContent(vbox);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                LocalTime time = LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue());
                return LocalDateTime.of(datePicker.getValue(), time);
            }
            return null;
        });

        return dialog.showAndWait();
    }
}