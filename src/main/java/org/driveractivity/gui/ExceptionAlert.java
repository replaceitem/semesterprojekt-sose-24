package org.driveractivity.gui;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.driveractivity.exception.AlertedException;

import java.io.*;

public class ExceptionAlert extends Alert {
    public ExceptionAlert(AlertedException exception) {
        super(AlertType.ERROR);
        setTitle(exception.getTitle());
        setHeaderText(exception.getTitle());
        setContentText(exception.getMessage());

        // if there are more details, show the full stack trace
        if(exception.getCause() != null) {
            System.err.println("Caught " + exception.getCause().getClass().getName() + " wrapped in " + exception.getClass().getName() + ":");
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

            getDialogPane().setExpandableContent(expContent);
        }
    }
}
