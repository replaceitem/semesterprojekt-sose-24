package org.driveractivity.service;

import org.driveractivity.exception.FileExportException;
import org.driveractivity.gui.ExceptionAlert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.*;
import java.util.Properties;

public class PropertiesService {
    static Path parentPath = Path.of(System.getProperty("user.home")).resolve("DriverActivityVisualization");
    static File propertiesFile = parentPath.resolve("app.properties").toFile();
    static File tempFile = parentPath.resolve("tmpFile.xml").toFile();
    
    private static Properties appProperties = new Properties();

    public static void loadProperties() {
        try{
            parentPath.toFile().mkdirs();
            
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
                saveProperty("openFilePath", System.getProperty("user.home"));
                saveProperty("saveFilePath", System.getProperty("user.home"));

                saveProperty("renderSpecificConditions", true);
                saveProperty("renderCardStatus", true);
                saveProperty("renderWeekDividers", true);
                saveProperty("renderDayDividers", true);
                saveProperty("applyRules", true);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void saveProperty(String key, String value) {
        appProperties.setProperty(key, value);
    }
    public static void saveProperty(String key, boolean value) {
        appProperties.setProperty(key, String.valueOf(value));
    }
    
    public static String getProperty(String key) {
        return appProperties.getProperty(key);
    }
    public static boolean getBooleanProperty(String key) {
        return Boolean.parseBoolean(appProperties.getProperty(key, "false"));
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
        if(tempFile.exists()){
            try{
                DriverInterface driverInterface = DriverService.getInstance();
                driverInterface.importFrom(tempFile);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void saveTmpFile(){
        DriverInterface driverInterface = DriverService.getInstance();
        try {
            driverInterface.exportToXML(tempFile);
        } catch (FileExportException e) {
            new ExceptionAlert(e).showAndWait();
        }
    }

}
