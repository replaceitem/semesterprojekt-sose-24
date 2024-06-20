package org.driveractivity.service;

import org.driveractivity.entity.Activity;
import org.driveractivity.entity.SpecificCondition;
import org.driveractivity.exception.FileImportException;
import org.driveractivity.exception.SpecificConditionException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public interface DriverInterface {

    ArrayList<Activity> getBlocks();
    void addBlock(Activity activity);
    void addBlock(int index, Activity activity);
    void removeBlock(int index);
    void changeBlock(int index, Activity activity);
    ArrayList<SpecificCondition> getSpecificConditions();
    ArrayList<SpecificCondition> addSpecificCondition(List<SpecificCondition> specificCondition) throws SpecificConditionException;
    ArrayList<SpecificCondition> removeSpecificCondition(List<SpecificCondition> specificCondition) throws SpecificConditionException;
    ArrayList<SpecificCondition> changeSpecificCondition(SpecificCondition specificCondition);
    void moveBlock(int fromIndex, int toIndex);
    void addDriverServiceListener(DriverServiceListener listener);
    void clear();
    void exportToXML(File file);
    void importFrom(File f) throws FileImportException;
}
