package org.driveractivity.service;

import lombok.Getter;
import org.driveractivity.entity.Activity;

import java.util.ArrayList;

public class ActivityService {
    @Getter
    private final ArrayList<Activity> activities;
    private final ArrayList<DriverServiceListener> listeners;
    public ActivityService() {
        activities = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    public void addListener(DriverServiceListener listener) {
        listeners.add(listener);
    }

    public void addBlock(Activity activity) {
        if(!activities.isEmpty()) {
            Activity last = activities.getLast();
            activity.setStartTime(last.getEndTime());
        }
        addActivityInternal(activity);
        mergeAtIndex(activities.size()-1);
    }

    public void addBlock(int index, Activity activity) {
        if(index < 0 || index > activities.size()) {
            throw new IndexOutOfBoundsException();
        }
        if(activities.isEmpty()) {
            throw new UnsupportedOperationException("This method is to be used only to add blocks between some other blocks, this cannot happen if the list is empty");
        }

        if(index == 0) {
            activity.setStartTime(activities.getFirst().getStartTime());
        } else {
            activity.setStartTime(activities.get(index-1).getEndTime());
        }
        addActivityInternal(index, activity);

        index = mergeAtIndex(index);

        adaptStartTimes(index+1);
    }

    public void removeBlock(int index) {
        if(index < 0 || index >= activities.size()) {
            throw new IndexOutOfBoundsException();
        }
        removeActivityInternal(index);
        if(index != activities.size()) {
            index = mergeAtIndex(index);
        }
        if (index != 0) {
            adaptStartTimes(index);
        }
    }
    public void importActivities(ArrayList<Activity> activities) {
        addActivityInternal(activities);
        for(int i = 1; i+2 < activities.size(); i = i + 2) {
            mergeAtIndex(i);
        }
    }
    public void changeBlock(int index, Activity activity) {
        if(index < 0 || index >= activities.size()) {
            throw new IndexOutOfBoundsException();
        }

        Activity originalActivity = activities.get(index);
        originalActivity.setStartTime(activity.getStartTime());
        originalActivity.setDuration(activity.getDuration());
        originalActivity.setCardStatus(activity.getCardStatus());
        originalActivity.setType(activity.getType());

        int finalIndex = index;
        listeners.forEach(l -> l.onActivityUpdated(finalIndex));

        index = mergeAtIndex(index);

        adaptStartTimes(index+1);
    }
    public void clear(){
        this.activities.clear();
        listeners.forEach(l -> l.onAllActivitiesUpdated(activities));
    }

    private void adaptStartTimes(int startIndex) {
        for(int i = startIndex; i < activities.size(); i++) {
            activities.get(i).setStartTime(activities.get(i-1).getEndTime());
            final int finalIndex = i;
            listeners.forEach(l -> l.onActivityUpdated(finalIndex));
        }
    }


    private void addActivityInternal(int index, Activity activity) {
        activities.add(index, activity);
        listeners.forEach(l -> l.onActivityAdded(index, activity));
    }


    private void addActivityInternal(Activity activity) {
        activities.add(activity);
        listeners.forEach(l -> l.onActivityAdded(activities.indexOf(activity), activity));
    }

    private void removeActivityInternal(int index) {
        activities.remove(index);
        listeners.forEach(l -> l.onActivityRemoved(index));
    }

    private void addActivityInternal(ArrayList<Activity> activities) {
        //This method is only used in case activities get loaded from xml
        this.activities.addAll(activities);
        listeners.forEach(l -> l.onAllActivitiesUpdated(this.activities));
    }

    private int mergeAtIndex(int index) {
        ArrayList<Activity> toMerge = new ArrayList<>();
        if(activities.size() < 2) {
            return index;
        }
        toMerge.add(activities.get(index));
        if(index+1 < activities.size()) {
            if(activities.get(index).canMergeWith(activities.get(index+1))) {
                toMerge.add(activities.get(index+1));
            }
        }
        if(index-1 >= 0) {
            if(activities.get(index).canMergeWith(activities.get(index-1))) {
                toMerge.addFirst(activities.get(index-1));
            }
        }
        for(int i = toMerge.size()-1; i > 0; i--) {
            toMerge.get(i-1).setDuration(toMerge.get(i-1).getDuration().plus(toMerge.get(i).getDuration()));
            removeActivityInternal(activities.indexOf(toMerge.get(i)));
        }
        int mergedActivityIndex = activities.indexOf(toMerge.getFirst());
        if(toMerge.size() > 1) {
            notifyListenersOfMerge(mergedActivityIndex);
        }
        return mergedActivityIndex;
    }

    private void notifyListenersOfMerge(int index) {
        for(DriverServiceListener listener : listeners) {
            listener.onActivitiesMerged(index);
        }
    }
}
