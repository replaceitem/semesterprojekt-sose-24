package org.driveractivity.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.driveractivity.service.DriverInterface;
import org.driveractivity.service.DriverService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class MainApplication extends javafx.application.Application {

    static Properties appProperties = new Properties();

    @Override
    public void start(Stage stage) throws IOException {

        DriverInterface driverInterface = DriverService.getInstance();
        if(new File(MainApplication.appProperties.getProperty("saveFilePath") + "/tmpFile").exists()){
            try{
                driverInterface.importFrom(new File(MainApplication.appProperties.getProperty("saveFilePath") + "/tmpFile.xml"));
            }catch(Exception e){
                e.printStackTrace();
            }
        }


        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 800);

        MainController controller = fxmlLoader.getController();

        controller.setStage(stage);

        stage.setTitle("Driver Activity Visualization");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        DriverInterface driverInterface = DriverService.getInstance();
        driverInterface.exportToXML(new File(MainApplication.appProperties.getProperty("saveFilePath") + "/tmpFile.xml"));
        System.exit(0);
    }

    public static void main(String[] args) {
        File file = new File(System.getProperty("user.home") + "/DriverTestApp/app.properties");

        try{
            File parentDir = file.getParentFile();
            if(parentDir != null && !parentDir.exists()){
                parentDir.mkdirs();
            }
            if(file.exists()){
                try{
                    appProperties.load(new FileInputStream(file));
                    System.out.println("Properties loaded");
                    appProperties.forEach((k,v)->{
                        System.out.println(k + " : " + v);
                    });
                }catch (Exception e){
                    System.out.println("Failed to load app properties");
                }
            }else{
                appProperties.setProperty("openFilePath", System.getProperty("user.home"));
                appProperties.setProperty("saveFilePath", System.getProperty("user.home"));

                appProperties.setProperty("renderSpecificConditions", "true");
                appProperties.setProperty("renderCardStatus", "true");
                appProperties.setProperty("renderWeekDividers", "true");
                appProperties.setProperty("renderDayDividers", "true");
                try{
                    appProperties.store(new FileOutputStream(file), "Default App Properties");
                    System.out.println("New properties saved");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        launch();
    }
}