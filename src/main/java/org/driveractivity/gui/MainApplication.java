package org.driveractivity.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.driveractivity.service.PropertiesService;

import java.io.IOException;

public class MainApplication extends javafx.application.Application {

    @Override
    public void start(Stage stage) throws IOException {

        PropertiesService.loadProperties();
        PropertiesService.loadTmpFile();

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 800);

        MainController controller = fxmlLoader.getController();

        controller.setStage(stage);

        stage.setTitle("Driver Activity Visualization");
        stage.getIcons().addAll(Icons.APP_ICONS);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        PropertiesService.saveTmpFile();
        PropertiesService.saveProperties();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }
}