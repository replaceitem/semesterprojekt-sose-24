package org.driveractivity.service;

import lombok.Getter;
import org.driveractivity.DTO.ITFTestFileDTO;
import org.driveractivity.entity.Activity;
import org.driveractivity.entity.ActivityGroup;
import org.driveractivity.mapper.ObjectToXmlDtoMapper;
import org.driveractivity.mapper.XmlDtoToObjectMapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;

@Getter
public class DriverService implements DriverInterface {
    private final ArrayList<Activity> activities;
    @Getter
    private static final DriverService instance = new DriverService();

    private DriverService() {
        activities = new ArrayList<>();
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
            activity.setStartTime(activities.getFirst().getStartTime());
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
        if(index < 0 || index >= activities.size()) {
            throw new IndexOutOfBoundsException();
        }
        if(index != 0 && index+1 != activities.size()) {
            Activity activityBefore = activities.get(index-1);
            Activity activityAfter = activities.get(index+1);
            if(activityBefore.getType() == activityAfter.getType()) {
                activityBefore.setDuration(activityBefore.getDuration().plus(activityAfter.getDuration()));
                activities.remove(activityAfter);
            }
        }
        activities.remove(index);
        if (index != 0) {
            for(int i = index; i < activities.size(); i++) {
                activities.get(i).setStartTime(activities.get(i-1).getEndTime());
            }
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

        if(index != 0) {
            Activity activityBefore = activities.get(index-1);
            if(activityBefore.getType() == activity.getType()) {
                activityBefore.setDuration(activityBefore.getDuration().plus(activity.getDuration()));
                removeBlock(index);
                index = index-1;
            }
        }

        if(index+1 != activities.size()) {
            Activity activityAfter = activities.get(index+1);
            if(activityAfter.getType() == activity.getType()) {
                activityAfter.setStartTime(activity.getEndTime());
                activityAfter.setDuration(activityAfter.getDuration().plus(activity.getDuration()));
                removeBlock(index);
            }
        }

        for(int i = index+1; i < activities.size(); i++) {
            activities.get(i).setStartTime(activities.get(i-1).getEndTime());
        }
        return activities;
    }

    @Override
    public void exportToXML() {
        ITFTestFileDTO itfTestFileDTO = ObjectToXmlDtoMapper.mapToXmlDto(activities);
        String filename = itfTestFileDTO.getActivityGroup().getDays().getFirst().getDate() + "-"+ itfTestFileDTO.getActivityGroup().getDays().getLast().getDate();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ITFTestFileDTO.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(itfTestFileDTO, new File(filename + ".xml"));
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<Activity> importFrom(File f) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ITFTestFileDTO.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            ITFTestFileDTO itfTestFileDTO = (ITFTestFileDTO) unmarshaller.unmarshal(f);
            ActivityGroup group = XmlDtoToObjectMapper.map(itfTestFileDTO.getActivityGroup());
            ArrayList<Activity> activities = new ArrayList<>(XmlDtoToObjectMapper.mapDayToActivity(group.getDays()));
            this.activities.clear();
            this.activities.addAll(activities);
            return activities;
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
