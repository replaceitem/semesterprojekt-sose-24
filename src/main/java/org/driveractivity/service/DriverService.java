package org.driveractivity.service;

import org.driveractivity.entity.Activity;

import java.io.File;
import java.util.ArrayList;

public class DriverService implements DriverInterface {
    private ArrayList<Activity> activities;

    public DriverService() {
        activities = new ArrayList<>();
    }

    @Override
    public ArrayList<Activity> addBlock(Activity activity) {
        if(!activities.isEmpty()) {
            Activity last = activities.get(activities.size()-1);
            activity.setStartTime(last.getEndTime());
        }
        activities.add(activity);
        return activities;
    }

    @Override
    public ArrayList<Activity> addBlock(int index, Activity activity) {
        if(index < 0 || index > activities.size()) {
            throw new IndexOutOfBoundsException();
        }
        // X X X X X X
        if(index == 0) {
            activity.setStartTime(activities.get(0).getStartTime());
        } else {
            activity.setStartTime(activities.get(index-1).getEndTime());
        }
        activities.add(index, activity);
        for(int i = index+1; i < activities.size(); i++) {
            activities.get(i).setStartTime(activities.get(i-1).getEndTime());
        }
        return activities;
    }

    @Override
    public ArrayList<Activity> removeBlock(int index) {
        return null;
    }

    @Override
    public ArrayList<Activity> changeBlock(int index) {
        return null;
    }

    @Override
    public void exportToXML() {

    }

    @Override
    public void importFrom(File f) {

    }

    public ArrayList<Activity> getActivities() {
        return activities;
    }
}
