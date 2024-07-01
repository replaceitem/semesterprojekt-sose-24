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
import org.driveractivity.exception.FileExportException;
import org.driveractivity.exception.FileImportException;
import org.driveractivity.exception.SpecificConditionException;
import org.driveractivity.mapper.ObjectToXmlDtoMapper;
import org.driveractivity.mapper.XmlDtoToObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class DriverService implements DriverInterface {

    private final ActivityService activityService;
    private final SpecificConditionService specificConditionService;
    @Getter
    private static final DriverService instance = new DriverService();
    private final ArrayList<DriverServiceListener> listeners;

    private DriverService() {
        listeners = new ArrayList<>();
        activityService = new ActivityService();
        specificConditionService = new SpecificConditionService(activityService);
    }

    @Override
    public ArrayList<Activity> getBlocks() {
        return activityService.getActivities();
    }

    @Override
    public void addBlock(Activity activity) {
        activityService.addBlock(activity);

    }

    @Override
    public void addBlock(int index, Activity activity) {
        activityService.addBlock(index, activity);
    }

    @Override
    public void removeBlock(int index) {
        activityService.removeBlock(index);
    }

    @Override
    public void changeBlock(int index, Activity activity) {
        activityService.changeBlock(index, activity);
    }

    @Override
    public ArrayList<SpecificCondition> getSpecificConditions() {
        return specificConditionService.getSpecificConditions();
    }

    @Override
    public ArrayList<SpecificCondition> addSpecificConditions(List<SpecificCondition> inputConditions) throws SpecificConditionException {
        return specificConditionService.addSpecificConditions(inputConditions);
    }


    @Override
    public ArrayList<SpecificCondition> removeSpecificConditions(SpecificCondition inputCondition) {
        return specificConditionService.removeSpecificConditions(inputCondition);
    }

    @Override
    public void addDriverServiceListener(DriverServiceListener listener) {
        listeners.add(listener);
        activityService.addListener(listener);
        specificConditionService.addListener(listener);
    }


    @Override
    public void clear(){
        specificConditionService.clear();
        activityService.clear();
        listeners.forEach(l -> l.onAllActivitiesUpdated(activityService.getActivities()));
    }

    @Override
    public void exportToXML(File file) throws FileExportException {
        try {
            ITFTestFileDTO itfTestFileDTO = ObjectToXmlDtoMapper.mapToXmlDto(activityService.getActivities(), specificConditionService.getSpecificConditions());
            JAXBContext jaxbContext = JAXBContext.newInstance(ITFTestFileDTO.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(itfTestFileDTO,file);
        } catch (Exception e) {
            throw new FileExportException("Error exporting to " + file.getName(), e);
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
            Optional.ofNullable(itfTestFileDTO.getSpecificConditionsDTO())
                    .ifPresent(dto -> specificConditionService.getSpecificConditions().addAll(XmlDtoToObjectMapper.mapSpecificConditions(dto)));

            //Read the activities
            ActivityGroup group = XmlDtoToObjectMapper.mapActivityGroup(itfTestFileDTO.getActivityGroup());
            ArrayList<Activity> activities = XmlDtoToObjectMapper.mapDayToActivity(group.getDays());
            activityService.importActivities(activities);
        } catch (JAXBException e) {
            throw new FileImportException("Error while importing file, please check if the file is valid.");
        }
    }

    @Override
    public void setRulesEnabled(boolean enabled) {
        specificConditionService.setRulesEnabled(enabled);
    }
}
