package org.driveractivity.service;

import org.driveractivity.entity.Activity;

import java.io.File;
import java.util.ArrayList;

public interface DriverInterface {
    ArrayList<Activity> addBlock(Activity activity);
    ArrayList<Activity> addBlock(int index, Activity activity);
    ArrayList<Activity> removeBlock(int index);
    ArrayList<Activity> changeBlock(int index);
    void exportToXML();
    void importFrom(File f);
}
