package org.driveractivity.service;

import org.driveractivity.entity.Activity;
import org.driveractivity.exception.FileImportException;

import java.io.File;
import java.util.ArrayList;

public interface DriverInterface {

    ArrayList<Activity> getBlocks();
    ArrayList<Activity> addBlock(Activity activity);
    ArrayList<Activity> addBlock(int index, Activity activity);
    ArrayList<Activity> removeBlock(int index);
    ArrayList<Activity> changeBlock(int index, Activity activity);
    void addDriverServiceListener(DriverServiceListener listener);
    void clearList();
    void exportToXML(File file);
    ArrayList<Activity> importFrom(File f) throws FileImportException;
}
