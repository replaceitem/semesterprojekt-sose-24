package org.driveractivity.gui;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import org.driveractivity.entity.*;
import org.kordamp.ikonli.*;
import org.kordamp.ikonli.javafx.*;

import java.time.format.*;

public class SpecificConditionEntry extends HBox {
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public SpecificConditionEntry(MainController mainController, SpecificCondition specificCondition) {
        FontIcon icon = getIconForSpecificConditionType(specificCondition.getSpecificConditionType());
        Label timestamp = new Label(specificCondition.getTimestamp().format(DATE_TIME_FORMATTER));
        FontIcon deleteIcon = Icons.create(Icons.DELETE, 16);
        deleteIcon.setIconColor(Color.RED);
        Button deleteButton = new Button();
        deleteButton.setGraphic(deleteIcon);
        deleteButton.setPadding(new Insets(4));
        deleteButton.setOnAction(actionEvent -> {
            mainController.driverInterface.removeSpecificCondition(specificCondition);
            mainController.loadSpecificConditions();
        });

        timestamp.setTextAlignment(TextAlignment.CENTER);
        Pane spacingPane = new Pane();
        setHgrow(spacingPane, Priority.ALWAYS);
        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(icon, timestamp, spacingPane, deleteButton);
    }
    
    
    private static FontIcon getIconForSpecificConditionType(SpecificConditionType type) {
        FontIcon fontIcon = new FontIcon();
        fontIcon.setIconColor(type.isBegin() ? Color.DARKGREEN : Color.DARKRED);
        Ikon icon = switch (type.getCondition()) {
            case FT -> Icons.FERRY_TRAIN;
            case OUT_OF_SCOPE -> Icons.OUT_OF_SCOPE;
        };
        fontIcon.setIconCode(icon);
        fontIcon.setIconSize(16);
        return fontIcon;
    }
}
