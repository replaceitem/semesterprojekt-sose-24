package org.driveractivity.gui;

import javafx.scene.control.Alert;
import org.driveractivity.exception.AlertedException;

public class AlertedExceptionDialog {
    public static void show(AlertedException exception) {
        exception.printStackTrace(System.err);
        showSilently(exception);
    }
    
    public static void showSilently(AlertedException exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(exception.getTitle());
        alert.setHeaderText(exception.getTitle());
        alert.setContentText(exception.getMessage());
        alert.show();
    }
}
