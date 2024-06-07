package org.driveractivity.gui;

import org.driveractivity.entity.Activity;
import org.driveractivity.service.DriverInterface;

import java.util.List;

public interface ActivityDisplay {
    void load(DriverInterface driverData);

    void reload(List<Activity> newActivities);

    DriverInterface getDriverInterface();
}
