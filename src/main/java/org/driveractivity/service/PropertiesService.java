package org.driveractivity.service;

import org.driveractivity.exception.FileExportException;
import org.driveractivity.gui.ExceptionAlert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class PropertiesService {

    static File propertiesFile = new File(System.getProperty("user.home") + "/DriverActivityVisualization/app.properties");
    static File parentDir = new File(System.getProperty("user.home") + "/DriverActivityVisualization/");
    public static Properties appProperties = new Properties();

    public static void loadProperties() {
        try{
            if(parentDir != null && !parentDir.exists()){
                parentDir.mkdirs();
            }
            if(propertiesFile.exists()){
                try{
                    appProperties.load(new FileInputStream(propertiesFile));
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
                appProperties.setProperty("applyRules", "true");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void saveProperty(String key, String value) {
        appProperties.setProperty(key, value);
    }

    public static void saveProperties(){
        try{
            appProperties.store(new FileOutputStream(propertiesFile), "App Properties");
            System.out.println("New properties saved");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void loadTmpFile(){
        if(new File(appProperties.getProperty("saveFilePath") + "/tmpFile.xml").exists()){
            try{
                DriverInterface driverInterface = DriverService.getInstance();
                driverInterface.importFrom(new File(appProperties.getProperty("saveFilePath") + "/tmpFile.xml"));
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void saveTmpFile(){
        DriverInterface driverInterface = DriverService.getInstance();
        try {
            driverInterface.exportToXML(new File(appProperties.getProperty("saveFilePath") + "/tmpFile.xml"));
        } catch (FileExportException e) {
            new ExceptionAlert(e).showAndWait();
        }
    }

}
