package org.driveractivity.service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import lombok.Getter;
import org.driveractivity.DTO.ITFTestFileDTO;
import org.driveractivity.entity.Activity;
import org.driveractivity.entity.ActivityGroup;
import org.driveractivity.entity.SpecificCondition;
import org.driveractivity.exception.FileImportException;
import org.driveractivity.mapper.ObjectToXmlDtoMapper;
import org.driveractivity.mapper.XmlDtoToObjectMapper;

import java.io.File;
import java.util.ArrayList;

@Getter
public class DriverService implements DriverInterface {
    private final ArrayList<Activity> activities;
    private final ArrayList<SpecificCondition> specificConditions;
    @Getter
    private static final DriverService instance = new DriverService();
    private final ArrayList<DriverServiceListener> listeners;

    private DriverService() {
        specificConditions = new ArrayList<>();
        activities = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    @Override
    public ArrayList<Activity> getBlocks() {
        return activities;
    }

    @Override
    public void addBlock(Activity activity) {
        if(!activities.isEmpty()) {
            Activity last = activities.getLast();
            activity.setStartTime(last.getEndTime());
        }
        addActivityInternal(activity);
        mergeAtIndex(activities.size()-1);
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    public void addSpecificCondition(SpecificCondition specificCondition) {
        specificConditions.add(specificCondition);
    }

    @Override
    public void removeSpecificCondition(SpecificCondition specificCondition) {
        specificConditions.remove(specificCondition);
    }

    @Override
    public void addDriverServiceListener(DriverServiceListener listener) {
        listeners.add(listener);
    }


    @Override
    public void clear(){
        this.specificConditions.clear();
        this.activities.clear();
        listeners.forEach(l -> l.onAllActivitiesUpdated(activities));
    }

    @Override
    public void exportToXML(File file) {
        ITFTestFileDTO itfTestFileDTO = ObjectToXmlDtoMapper.mapToXmlDto(activities, specificConditions);
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ITFTestFileDTO.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(itfTestFileDTO,file);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void importFrom(File f) throws FileImportException {
        try {
            //Read the file
            JAXBContext jaxbContext = JAXBContext.newInstance(ITFTestFileDTO.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            ITFTestFileDTO itfTestFileDTO = (ITFTestFileDTO) unmarshaller.unmarshal(f);

            clear();
            
            //Read the specific conditions
            specificConditions.addAll(XmlDtoToObjectMapper.mapSpecificConditions(itfTestFileDTO.getSpecificConditionsDTO()));
            
            //Read the activities
            ActivityGroup group = XmlDtoToObjectMapper.mapActivityGroup(itfTestFileDTO.getActivityGroup());
            ArrayList<Activity> activities = XmlDtoToObjectMapper.mapDayToActivity(group.getDays());
            addActivityInternal(activities);
            for(int i = 1; i+2 < activities.size(); i = i + 2) {
                mergeAtIndex(i);
            }
        } catch (JAXBException e) {
            throw new FileImportException("Error while importing file, please check if the file is valid.");
        }
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
