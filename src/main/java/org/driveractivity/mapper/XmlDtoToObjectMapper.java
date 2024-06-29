package org.driveractivity.mapper;

import org.driveractivity.DTO.*;
import org.driveractivity.entity.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;

public class XmlDtoToObjectMapper {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public static ActivityGroup mapActivityGroup(ActivityGroupDTO dto) {
        return ActivityGroup.builder()
                .days(mapDays(dto.getDays())).build();
    }
    
    public static ArrayList<SpecificCondition> mapSpecificConditions(SpecificConditionsDTO dto) {
        return dto.getSpecificConditions().stream().map(XmlDtoToObjectMapper::mapSpecificCondition).collect(Collectors.toCollection(ArrayList::new));
    }

    private static SpecificCondition mapSpecificCondition(SpecificConditionDTO specificConditionDTO) {
        return SpecificCondition.builder()
                .timestamp(LocalDateTime.parse(specificConditionDTO.getTimestamp(), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))
                .specificConditionType(SpecificConditionType.mapType(specificConditionDTO.getSpecificConditionType()))
                .build();
    }

    public static ArrayList<Activity> mapDayToActivity(ArrayList<Day> days) {
        Iterator<Day> it = days.iterator();
        Day current = it.next();
        Day next;
        while(it.hasNext()) {
            next = it.next();
            if(current.getActivities().getLast().getType() == next.getActivities().getFirst().getType()) {
                current.getActivities().getLast().setDuration(Duration.between(current.getActivities().getLast().getStartTime(), next.getActivities().getFirst().getEndTime()));
                next.getActivities().removeFirst();
            }
            if(!next.getActivities().isEmpty()) current = next;
        }
        return days.stream().flatMap(day -> day.getActivities().stream()).collect(Collectors.toCollection(ArrayList::new));
    }

    private static ArrayList<Day> mapDays(ArrayList<DayDTO> days) {
        return days.stream().map(XmlDtoToObjectMapper::mapDay).collect(Collectors.toCollection(ArrayList::new));
    }
    private static Day mapDay(DayDTO dayDTO) {
        return Day.builder()
                .date(LocalDate.parse(dayDTO.getDate(), formatter))
                .presenceCounter(dayDTO.getPresenceCounter())
                .distance(dayDTO.getDistance())
                .activities(mapActivities(dayDTO.getActivities(), LocalDate.parse(dayDTO.getDate(), formatter)))
                .build();
    }
    private static ArrayList<Activity> addDurationToActivities(ArrayList<Activity> activities) {
        var it = activities.iterator();
        Activity previous = null;
        while(it.hasNext()) {
            Activity current = it.next();
            if(previous != null) previous.setDuration(Duration.between(previous.getStartTime(), current.getStartTime()));
            previous = current;
            if(!it.hasNext()) {
                previous.setDuration(Duration.between(previous.getStartTime(), LocalDateTime.of(previous.getStartTime().toLocalDate(), LocalTime.of(23, 59))));
            }
        }
        return activities;
    }
    private static ArrayList<Activity> mapActivities(ArrayList<ActivityDTO> activities, LocalDate date) {
        return addDurationToActivities(activities.stream().map(x -> mapActivity(x, date)).collect(Collectors.toCollection(ArrayList::new)));
    }
    private static Activity mapActivity(ActivityDTO activityDTO, LocalDate date) {
        return Activity.builder()
                .type(ActivityType.valueOf(activityDTO.getActivity().toUpperCase()))
                .startTime(LocalDateTime.of(date, LocalTime.parse(activityDTO.getTime())))
                .cardStatus(CardStatus.mapNameFromString(activityDTO.getCardStatus()))
                .build();
    }

}
