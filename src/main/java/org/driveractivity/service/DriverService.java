package org.driveractivity.service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import lombok.Getter;
import org.driveractivity.DTO.ITFTestFileDTO;
import org.driveractivity.entity.Activity;
import org.driveractivity.entity.ActivityGroup;
import org.driveractivity.exception.FileImportException;
import org.driveractivity.mapper.ObjectToXmlDtoMapper;
import org.driveractivity.mapper.XmlDtoToObjectMapper;

import java.io.File;
import java.util.ArrayList;

@Getter
public class DriverService implements DriverInterface {
    private final ArrayList<Activity> activities;
    @Getter
    private static final DriverService instance = new DriverService();
    private final ArrayList<DriverServiceListener> listeners;

    private DriverService() {
        activities = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    @Override
    public ArrayList<Activity> getBlocks() {
        return activities;
    }

    @Override
    public ArrayList<Activity> addBlock(Activity activity) {
        if(!activities.isEmpty()) {
            Activity last = activities.getLast();
            activity.setStartTime(last.getEndTime());
        }
        addActivityInternal(activity);
        mergeAtIndex(activities.size()-1);
        return activities;
    }

    @Override
    public ArrayList<Activity> addBlock(int index, Activity activity) {
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
        return activities;
    }

    @Override
    public ArrayList<Activity> removeBlock(int index) {
        if(index < 0 || index >= activities.size()) {
            throw new IndexOutOfBoundsException();
        }
        removeActivityInternal(index);
        index = mergeAtIndex(index);
        if (index != 0) {
            adaptStartTimes(index);
        }
        return activities;
    }

    @Override
    public ArrayList<Activity> changeBlock(int index, Activity activity) {
        if(index < 0 || index >= activities.size()) {
            throw new IndexOutOfBoundsException();
        }

        activities.get(index).setDuration(activity.getDuration());
        activities.get(index).setType(activity.getType());

        int finalIndex = index;
        listeners.forEach(l -> l.onActivityUpdated(finalIndex));

        index = mergeAtIndex(index);

        adaptStartTimes(index+1);
        return activities;
    }

    @Override
    public void addDriverServiceListener(DriverServiceListener listener) {
        listeners.add(listener);
    }


    @Override
    public void clearList(){
        activities.clear();
    }

    @Override
    public void exportToXML(File file) {
        ITFTestFileDTO itfTestFileDTO = ObjectToXmlDtoMapper.mapToXmlDto(activities);
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
    public ArrayList<Activity> importFrom(File f) throws FileImportException {
        //TODO 2 types can be near one another - DONE
        //TODO presenceCounter is a counter of days day 0 - presenceCounter 0, day 1 - presenceCounter 1, etc. - DONE
        //TODO cardStatus can either be "notInserted" or "inserted" - DONE
        //TODO make specificConditions: two most important ones: outOfScope and FT (Ferry Train), FT does not necessarily have an end
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ITFTestFileDTO.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            ITFTestFileDTO itfTestFileDTO = (ITFTestFileDTO) unmarshaller.unmarshal(f);
            ActivityGroup group = XmlDtoToObjectMapper.map(itfTestFileDTO.getActivityGroup());
            ArrayList<Activity> activities = new ArrayList<>(XmlDtoToObjectMapper.mapDayToActivity(group.getDays()));
            this.activities.clear();
            addActivityInternal(activities);
            for(int i = 1; i+2 < activities.size(); i = i + 2) {
                mergeAtIndex(i);
            }
            return activities;
        } catch (JAXBException e) {
            throw new FileImportException("Error while importing file, please check if the file is valid.");
        }
    }

    private void adaptStartTimes(int startIndex) {
        ArrayList<Activity> changedActivities = new ArrayList<>();
        for(int i = startIndex; i < activities.size(); i++) {
            activities.get(i).setStartTime(activities.get(i-1).getEndTime());
            changedActivities.add(activities.get(i));
        }
        listeners.forEach(l -> l.onActivitiesUpdated(changedActivities));
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
        activities.forEach(a -> listeners.forEach(l -> l.onActivityAdded(activities.indexOf(a), a)));
    }


    private int mergeAtIndex(int index) {
        ArrayList<Activity> toMerge = new ArrayList<>();
        if(activities.size() < 2) {
            return index;
        }
        toMerge.add(activities.get(index));
        if(index+1 < activities.size()) {
            if(activities.get(index).getType() == activities.get(index+1).getType()) {
                toMerge.add(activities.get(index+1));
            }
        }
        if(index-1 >= 0) {
            if(activities.get(index).getType() == activities.get(index-1).getType()) {
                toMerge.addFirst(activities.get(index-1));
            }
        }
        for(int i = toMerge.size()-1; i > 0; i--) {
            toMerge.get(i-1).setDuration(toMerge.get(i-1).getDuration().plus(toMerge.get(i).getDuration()));
            activities.remove(toMerge.get(i));
        }
        int mergedActivityIndex = toMerge.indexOf(toMerge.getFirst());
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
