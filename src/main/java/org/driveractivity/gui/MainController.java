package org.driveractivity.gui;

import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import lombok.*;
import org.driveractivity.entity.Activity;
import org.driveractivity.entity.ActivityType;
import org.driveractivity.entity.SpecificCondition;
import org.driveractivity.entity.SpecificConditionType;
import org.driveractivity.exception.FileExportException;
import org.driveractivity.exception.FileImportException;
import org.driveractivity.exception.SpecificConditionException;
import org.driveractivity.service.*;

import java.io.*;
import java.net.*;
import java.time.*;
import java.util.List;
import java.util.Optional;
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
    public VBox specificConditions;

    @FXML
    private OnOffToggleButton dayToggle;
    @FXML
    private OnOffToggleButton weekToggle;
    @FXML
    private OnOffToggleButton cardToggle;
    @FXML
    private OnOffToggleButton conditionToggle;
    @FXML
    private OnOffToggleButton applyRulesToggle;


    public DriverInterface driverInterface;
    @Setter
    private Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        activityPane.setMainController(this);
        driverInterface = DriverService.getInstance();
        activityPane.initialize(driverInterface);
        
        applyRulesToggle.selectedProperty().subscribe(aBoolean -> driverInterface.setRulesEnabled(aBoolean));
        activityPane.getRenderDayDividersProperty().bind(dayToggle.selectedProperty());
        activityPane.getRenderWeekDividersProperty().bind(weekToggle.selectedProperty());
        activityPane.getRenderCardStatusProperty().bind(cardToggle.selectedProperty());
        activityPane.getRenderSpecificConditionsProperty().bind(conditionToggle.selectedProperty());

        applyRulesToggle.setSelected(PropertiesService.getBooleanProperty(PropertiesService.APPLY_RULES));
        dayToggle.setSelected(PropertiesService.getBooleanProperty(PropertiesService.RENDER_DAY_DIVIDERS));
        weekToggle.setSelected(PropertiesService.getBooleanProperty(PropertiesService.RENDER_WEEK_DIVIDERS));
        cardToggle.setSelected(PropertiesService.getBooleanProperty(PropertiesService.RENDER_CARD_STATUS));
        conditionToggle.setSelected(PropertiesService.getBooleanProperty(PropertiesService.RENDER_SPECIFIC_CONDITIONS));
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
    private void clearAll() {
        driverInterface.clear();
        loadSpecificConditions();
    }

    public void openActivityEditorStage(ActivityType currentActivityType, int insertionIndex, Activity editActivity) {
        try {
            FXMLLoader loader = new FXMLLoader(ActivityEditor.class.getResource("activity-editor.fxml"));
            Parent root = loader.load();
            ActivityEditor controller = loader.getController();
            boolean success = controller.initialize(this, insertionIndex, currentActivityType, editActivity);
            if(!success) return;
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.DECORATED);
            dialogStage.setResizable(false);
            dialogStage.getIcons().addAll(Icons.APP_ICONS);
            dialogStage.setScene(new Scene(root, 450, 400));
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
        fileChooser.setInitialDirectory(new File(PropertiesService.getProperty(PropertiesService.OPEN_FILE_PATH)));

        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            PropertiesService.saveProperty(PropertiesService.OPEN_FILE_PATH, file.getParentFile().getAbsolutePath());

            try {
                driverInterface.importFrom(file);
                loadSpecificConditions();
            } catch (FileImportException e) {
                new ExceptionAlert(e).showAndWait();
            }
        }

    }

    @FXML
    private void saveFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save XML-File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        fileChooser.setInitialDirectory(new File(PropertiesService.getProperty(PropertiesService.SAVE_FILE_PATH)));

        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            PropertiesService.saveProperty(PropertiesService.SAVE_FILE_PATH, file.getParentFile().getAbsolutePath());

            try {
                driverInterface.exportToXML(file);
            } catch (FileExportException e) {
                new ExceptionAlert(e).showAndWait();
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
    public void toggleRules(ActionEvent event) {
        ToggleButton toggleButton = (ToggleButton) event.getSource();
        PropertiesService.saveProperty(PropertiesService.APPLY_RULES, toggleButton.isSelected());
    }

    @FXML
    private void toggleDayDivider(ActionEvent event){
        ToggleButton toggleButton = (ToggleButton) event.getSource();
        PropertiesService.saveProperty(PropertiesService.RENDER_DAY_DIVIDERS, toggleButton.isSelected());
    }

    @FXML
    public void toggleWeekDivider(ActionEvent event) {
        ToggleButton toggleButton = (ToggleButton) event.getSource();
        PropertiesService.saveProperty(PropertiesService.RENDER_WEEK_DIVIDERS, toggleButton.isSelected());
    }

    @FXML
    public void toggleCardStatus(ActionEvent event) {
        ToggleButton toggleButton = (ToggleButton) event.getSource();
        PropertiesService.saveProperty(PropertiesService.RENDER_CARD_STATUS, toggleButton.isSelected());
    }

    @FXML
    public void toggleSpecificConditions(ActionEvent event) {
        ToggleButton toggleButton = (ToggleButton) event.getSource();
        PropertiesService.saveProperty(PropertiesService.RENDER_SPECIFIC_CONDITIONS, toggleButton.isSelected());
    }

    public void onMoveForward() {
        moveActivity(1);
    }

    public void onMoveBackward() {
        moveActivity(-1);
    }
    
    private void moveActivity(int shift) {
        activityPane.getSelectedBlock().ifPresent(index -> {
            if(index < 0 || index > driverInterface.getBlocks().size() - 1) return;
            int shiftedIndex = index+shift;
            if(shiftedIndex < 0 || shiftedIndex > driverInterface.getBlocks().size() - 1) return;
            Activity activity = driverInterface.getBlocks().get(index);
            
            int sizeBefore = driverInterface.getBlocks().size();
            driverInterface.removeBlock(index);
            // if two blocks were removed, a merge must have happened,
            // and the merged block is now one index below selected which
            // needs to be considered for shifting forward only.
            if(driverInterface.getBlocks().size() < sizeBefore-1 && shift > 0) index--;
            index += shift;
            // constrain to valid index for insertion
            index = Math.max(0, Math.min(driverInterface.getBlocks().size(), index));
            
            sizeBefore = driverInterface.getBlocks().size();
            driverInterface.addBlock(index, activity);
            // if no merging happened during insertion, select the moved block
            if(sizeBefore+1 == driverInterface.getBlocks().size()) activityPane.setSelectedBlock(index);
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
        Optional<LocalDateTime> endOutOfScopeTime = openDateTimePicker("endOutOfScope time", "Choose a time for endOutOfScope specific condition");
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
            driverInterface.addSpecificConditions(List.of(beginCondition, endCondition));
        } catch (SpecificConditionException e) {
            new ExceptionAlert(e).showAndWait();
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
            driverInterface.addSpecificConditions(List.of(specificCondition));
        } catch (SpecificConditionException e) {
            new ExceptionAlert(e).showAndWait();
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
        hourSpinner.setEditable(true);
        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
        Spinner<Integer> minuteSpinner = new Spinner<>();
        hourSpinner.setEditable(true);
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