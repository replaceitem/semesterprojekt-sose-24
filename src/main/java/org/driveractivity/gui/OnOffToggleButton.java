package org.driveractivity.gui;

import javafx.beans.*;
import javafx.beans.property.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

@DefaultProperty("labelText")
public class OnOffToggleButton extends HBox {
    private final ToggleButton toggleButton;
    private final StringProperty labelText = new SimpleStringProperty(this, "text", "");
    
    public StringProperty labelTextProperty() {
        return labelText;
    }
    public String getLabelText() {
        return labelText.get();
    }
    public void setLabelText(String text) {
        labelText.set(text);
    }

    public ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
        return toggleButton.onActionProperty();
    }
    public EventHandler<ActionEvent> getOnAction() {
        return toggleButton.getOnAction();
    }
    public void setOnAction(EventHandler<ActionEvent> text) {
        toggleButton.setOnAction(text);
    }

    public BooleanProperty selectedProperty() {
        return toggleButton.selectedProperty();
    }
    public boolean isSelected() {
        return toggleButton.isSelected();
    }
    public void setSelected(boolean selected) {
        toggleButton.setSelected(selected);
    }
    
    public OnOffToggleButton() {
        this.setAlignment(Pos.CENTER);
        Label label = new Label();
        label.textProperty().bind(labelText);
        label.setMaxWidth(Double.POSITIVE_INFINITY);
        HBox.setHgrow(label, Priority.ALWAYS);
        
        toggleButton = new ToggleButton();
        toggleButton.textProperty().bind(toggleButton.selectedProperty().map(b -> b ? "ON" : "OFF"));
        
        getChildren().setAll(label, toggleButton);
    }
}
