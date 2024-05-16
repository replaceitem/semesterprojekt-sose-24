package org.driveractivity.service;

import org.driveractivity.entity.Activity;
import org.driveractivity.entity.Day;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public interface DriverInterface {

    ArrayList<Activity> getBlocks();
    ArrayList<Activity> addBlock(Activity activity);
    ArrayList<Activity> addBlock(int index, Activity activity);
    ArrayList<Activity> removeBlock(int index);
    ArrayList<Activity> changeBlock(int index);
    void exportToXML();
    ArrayList<Activity> importFrom(File f);
}
