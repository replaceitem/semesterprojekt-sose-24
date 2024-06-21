package org.driveractivity.gui;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.driveractivity.exception.AlertedException;

import java.io.*;

public class AlertedExceptionDialog {
    public static void show(AlertedException exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(exception.getTitle());
        alert.setHeaderText(exception.getTitle());
        alert.setContentText(exception.getMessage());
        
        if(exception.getCause() != null) {
            exception.printStackTrace(System.err);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            String exceptionText = sw.toString();

            Label label = new Label("The exception stacktrace was:");

            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setStyle("-fx-font-family: 'Monospaced'");

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);

            alert.getDialogPane().setExpandableContent(expContent);
        }
        
        alert.showAndWait();
    }
}
