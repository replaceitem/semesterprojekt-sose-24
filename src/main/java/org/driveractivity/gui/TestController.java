package org.driveractivity.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class TestController implements Initializable {
    @FXML
    public ActivityPane activityPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        activityPane.load(SampleData.getSampleData(40));
    }
}