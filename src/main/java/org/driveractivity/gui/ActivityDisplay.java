package org.driveractivity.gui;

import org.driveractivity.entity.Activity;
import org.driveractivity.service.DriverInterface;

import java.util.List;

public interface ActivityDisplay {
    void load(DriverInterface driverData);

    void reload(List<Activity> newActivities);

    void addBack(Activity activity);

    void addActivity(int index, Activity activity);

    void removeActivity(int index);

    DriverInterface getDriverInterface();
}
