package org.driveractivity.gui;

import org.driveractivity.entity.Activity;
import org.driveractivity.service.DriverInterface;

import java.time.LocalDateTime;

public interface ActivityDisplay {
    void load(DriverInterface driverData);

    void reload();

    void addBack(Activity activity);

    void addActivity(int index, Activity activity);

    void removeActivity(int index);

    LocalDateTime getStartTime();
}
