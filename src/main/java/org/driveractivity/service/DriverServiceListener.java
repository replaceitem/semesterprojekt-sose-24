package org.driveractivity.service;

import org.driveractivity.entity.Activity;

import java.util.List;

public interface DriverServiceListener {
    void onAllActivitiesUpdated(List<Activity> activities);

    /**
     * Triggered whenever an activity was removed
     * @param index Index of the removed activity
     */
    void onActivityRemoved(int index);

    /**
     * Triggered whenever an activity was added
     * @param index    Index where the activity was added
     * @param activity The added activity
     */
    void onActivityAdded(int index, Activity activity);

    /**
     * Triggered whenever any attribute of an activity was changed
     * @param index Index of the modified activity
     */
    void onActivityUpdated(int index);

    /**
     * Triggered whenever multiple activities were merged.
     * Must be called after {@link DriverServiceListener#onActivityRemoved(int)} was called for the removed activities
     * @param index Index of the resulting activity after being merged
     */
    void onActivitiesMerged(int index);
}
