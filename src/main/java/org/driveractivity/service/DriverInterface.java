package org.driveractivity.service;

import org.driveractivity.entity.Activity;
import org.driveractivity.entity.SpecificCondition;
import org.driveractivity.exception.*;

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
    ArrayList<SpecificCondition> removeSpecificCondition(SpecificCondition specificCondition) throws SpecificConditionException;
    void addDriverServiceListener(DriverServiceListener listener);
    void clear();
    void exportToXML(File file) throws FileExportException;
    void importFrom(File f) throws FileImportException;
}
