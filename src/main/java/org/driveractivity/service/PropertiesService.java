package org.driveractivity.service;

import org.driveractivity.exception.FileExportException;
import org.driveractivity.gui.ExceptionAlert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.*;
import java.util.Properties;

public class PropertiesService {
    
    public static final String OPEN_FILE_PATH = "openFilePath";
    public static final String SAVE_FILE_PATH = "saveFilePath";
    
    public static final String RENDER_SPECIFIC_CONDITIONS = "renderSpecificConditions";
    public static final String RENDER_CARD_STATUS = "renderCardStatus";
    public static final String RENDER_WEEK_DIVIDERS = "renderWeekDividers";
    public static final String RENDER_DAY_DIVIDERS = "renderDayDividers";
    public static final String APPLY_RULES = "applyRules";
    
    static final Path parentPath = Path.of(System.getProperty("user.home")).resolve("DriverActivityVisualization");
    static final File propertiesFile = parentPath.resolve("app.properties").toFile();
    static final File tempFile = parentPath.resolve("tmpFile.xml").toFile();
    
    private static final Properties appProperties = new Properties();

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
                saveProperty(OPEN_FILE_PATH, System.getProperty("user.home"));
                saveProperty(SAVE_FILE_PATH, System.getProperty("user.home"));

                saveProperty(RENDER_SPECIFIC_CONDITIONS, true);
                saveProperty(RENDER_CARD_STATUS, true);
                saveProperty(RENDER_WEEK_DIVIDERS, true);
                saveProperty(RENDER_DAY_DIVIDERS, true);
                saveProperty(APPLY_RULES, true);
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
