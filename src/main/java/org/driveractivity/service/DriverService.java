package org.driveractivity.service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import lombok.Getter;
import org.driveractivity.DTO.ITFTestFileDTO;
import org.driveractivity.entity.*;
import org.driveractivity.exception.FileExportException;
import org.driveractivity.exception.FileImportException;
import org.driveractivity.exception.SpecificConditionException;
import org.driveractivity.mapper.ObjectToXmlDtoMapper;
import org.driveractivity.mapper.XmlDtoToObjectMapper;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.driveractivity.entity.SpecificConditionType.*;
import static org.driveractivity.entity.SpecificConditionType.Condition.*;

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
    public ArrayList<SpecificCondition> addSpecificCondition(List<SpecificCondition> inputConditions) throws SpecificConditionException {

        SpecificCondition beginCondition = inputConditions.stream().filter(s -> s.getSpecificConditionType() == BEGIN_FT || s.getSpecificConditionType() == BEGIN_OUT_OF_SCOPE).findFirst().orElse(null);
        SpecificCondition endCondition = inputConditions.stream().filter(s -> s.getSpecificConditionType() == END_FT || s.getSpecificConditionType() == END_OUT_OF_SCOPE).findFirst().orElse(null);
        if(beginCondition != null && endCondition != null) {
            if(beginCondition.getTimestamp().isAfter(endCondition.getTimestamp())) {
                throw new SpecificConditionException("The BEGIN_FT or BEGIN_OUT_OF_SCOPE must take place before the END_FT or END_OUT_OF_SCOPE");
            }
        }

        //if input contains OUT OF SCOPE, it must contain both
        //also it should not be allowed to intersect with other out-of-scope conditions
        if(inputConditions.stream().anyMatch(i -> i.getSpecificConditionType().getCondition() == OUT_OF_SCOPE)) {
            if(hasIncompleteScopeConditions(inputConditions)) {
                throw new SpecificConditionException("If a BEGIN_OUT_OF_SCOPE is added, an END_OUT_OF_SCOPE must be added as well");
            } else {
                for(SpecificCondition s : specificConditions) {
                    if(s.getSpecificConditionType() == BEGIN_OUT_OF_SCOPE) {
                        if(isTimestampWithinRange(s, findNextSpecificConditionOfType(END_OUT_OF_SCOPE, s), inputConditions.getFirst().getTimestamp()) || isTimestampWithinRange(s, findNextSpecificConditionOfType(END_OUT_OF_SCOPE, s), inputConditions.getLast().getTimestamp()) ) {
                            throw new SpecificConditionException("The new OUT_OF_SCOPE conditions intersect with existing ones");
                        }
                    }
                }
            }
        }
        //if the conditions are of FT type
        if(inputConditions.stream().anyMatch(specificCondition -> specificCondition.getSpecificConditionType().getCondition() == FT)) {
            //if input is ONLY beginFT, make sure that it ends with the next DRIVING activity
            //if there is no following DRIVING activity, then this FT does not have an end, set the flag that it has no end
            if(beginCondition != null && endCondition == null) {
                Activity nextDriving = findNextActivityOfType(ActivityType.DRIVING, beginCondition.getTimestamp());
                if(nextDriving != null) {
                    inputConditions.add(SpecificCondition.builder()
                            .specificConditionType(END_FT)
                            .timestamp(nextDriving.getStartTime())
                            .build());
                    endCondition = inputConditions.getLast();
                } else {
                    beginCondition.setWithoutEnd(true);
                }
            }

            //Make sure they don't intersect with other FT conditions
            for(SpecificCondition s : specificConditions) {
                if(s.getSpecificConditionType() == BEGIN_FT && !s.isWithoutEnd()) {
                    if(beginCondition != null && isTimestampWithinRange(s, findNextSpecificConditionOfType(END_FT, s), beginCondition.getTimestamp()) && endCondition != null && isTimestampWithinRange(s, findNextSpecificConditionOfType(END_FT, s), endCondition.getTimestamp())) {
                        throw new SpecificConditionException("The new FT conditions intersect with existing ones");
                    }
                }
            }

            //if input is beginFT with endFT, they may not have a driving activity in between
            //if this is the case, the endFT should be set to the start of the next driving activity
            if(beginCondition != null && endCondition != null) {
                Activity nextDriving = findNextActivityOfType(ActivityType.DRIVING, beginCondition.getTimestamp());
                if(nextDriving != null) {
                    if(isTimestampWithinRange(beginCondition, endCondition, nextDriving.getStartTime())) {
                        endCondition.setTimestamp(nextDriving.getStartTime());
                    }
                }
            }

            //There may not be any further FT conditions added after an unclosed beginFT
            if(beginCondition != null) {
                SpecificCondition unclosedFT = specificConditions.stream()
                        .filter(specificCondition -> specificCondition.getSpecificConditionType().getCondition() == FT && specificCondition.isWithoutEnd())
                        .findFirst()
                        .orElse(null);
                if(unclosedFT != null && unclosedFT.getTimestamp().isBefore(beginCondition.getTimestamp())) {
                    throw new SpecificConditionException("Please close the unclosed FT before adding further FT conditions after it.");
                }
            }
            //If only an endCondition is added
            if(beginCondition == null && endCondition != null) {
                SpecificCondition lastFT = findUnclosedFTCondition();

                if(lastFT == null || lastFT.getTimestamp().isAfter(endCondition.getTimestamp())) {
                    throw new SpecificConditionException("If an END_FT is added, a BEGIN_FT must either already exist or be added before it.");
                } else {
                    lastFT.setWithoutEnd(false);
                }
            }
        }

        specificConditions.addAll(inputConditions);

        IntStream.range(0, activities.size()).forEach(i -> listeners.forEach(l -> l.onActivityUpdated(i)));
        return specificConditions;
    }


    @Override
    public ArrayList<SpecificCondition> removeSpecificCondition(SpecificCondition inputCondition) {
        ArrayList<SpecificCondition> toDelete = new ArrayList<>();
        toDelete.add(inputCondition);
        SpecificCondition correspondingCondition = null;
        if(inputCondition.getSpecificConditionType().isABeginning()) {
            correspondingCondition = findNextSpecificConditionOfType(inputCondition.getSpecificConditionType().getOpposite(), inputCondition);
        } else {
            correspondingCondition = findPreviousSpecificConditionOfType(inputCondition.getSpecificConditionType().getOpposite(), inputCondition);
        }
        if(correspondingCondition != null) {
            toDelete.add(correspondingCondition);
        }

        specificConditions.removeAll(toDelete);

        IntStream.range(0, activities.size()).forEach(i -> listeners.forEach(l -> l.onActivityUpdated(i)));
        return specificConditions;
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
    public void exportToXML(File file) throws FileExportException {
        try {
            ITFTestFileDTO itfTestFileDTO = ObjectToXmlDtoMapper.mapToXmlDto(activities, specificConditions);
            JAXBContext jaxbContext = JAXBContext.newInstance(ITFTestFileDTO.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(itfTestFileDTO,file);
        } catch (Exception e) {
            throw new FileExportException("Error exporting to " + file.getName(), e);
        }
    }

    private boolean isTimestampWithinRange(SpecificCondition begin, SpecificCondition end, LocalDateTime timestamp) {
        return (timestamp.isAfter(begin.getTimestamp()) || timestamp.isEqual(begin.getTimestamp())) && (timestamp.isBefore(end.getTimestamp()) || timestamp.isEqual(end.getTimestamp()));
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
                    .ifPresent(dto -> specificConditions.addAll(XmlDtoToObjectMapper.mapSpecificConditions(dto)));

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

    private boolean hasIncompleteScopeConditions(List<SpecificCondition> inputConditions) {
        return inputConditions.stream().filter(s -> s.getSpecificConditionType() == BEGIN_OUT_OF_SCOPE).count() != inputConditions.stream().filter(s -> s.getSpecificConditionType() == END_OUT_OF_SCOPE).count();
    }

    private SpecificCondition findUnclosedFTCondition() {
        return specificConditions.stream()
                .filter(SpecificCondition::isWithoutEnd)
                .findFirst()
                .orElse(null);
    }

    private SpecificCondition findNextSpecificConditionOfType(SpecificConditionType type, SpecificCondition inputCondition) {
        for(SpecificCondition specificCondition : specificConditions) {
            if(specificCondition.getSpecificConditionType() == type && (specificCondition.getTimestamp().isAfter(inputCondition.getTimestamp()) || specificCondition.getTimestamp().isEqual(inputCondition.getTimestamp()))) {
                return specificCondition;
            }
        }
        return null;
    }
    private SpecificCondition findPreviousSpecificConditionOfType(SpecificConditionType type, SpecificCondition inputCondition) {
        SpecificCondition previousCondition = specificConditions.getFirst();
        if(previousCondition == null) {
            return null;
        }
        for(SpecificCondition specificCondition : specificConditions) {
            if(specificCondition.getTimestamp().isBefore(inputCondition.getTimestamp()) && specificCondition.getSpecificConditionType() == type) {
                if(specificCondition.getTimestamp().isAfter(previousCondition.getTimestamp())) {
                    previousCondition = specificCondition;
                }
            }
        }
        return previousCondition;
    }

    private Activity findNextActivityOfType(ActivityType type, LocalDateTime timestamp) {
        for(Activity activity : activities) {
            if(activity.getType() == type && (activity.getStartTime().isAfter(timestamp) || activity.getStartTime().isEqual(timestamp))) {
                return activity;
            }
        }
        return null;
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
