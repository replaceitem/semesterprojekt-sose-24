package org.driveractivity.service;

import org.driveractivity.entity.Activity;

import java.util.List;

public interface DriverServiceListener {
    void onActivitiesUpdated(List<Activity> activities);
    void onActivitiesMerged(List<Integer> indexes);
}
