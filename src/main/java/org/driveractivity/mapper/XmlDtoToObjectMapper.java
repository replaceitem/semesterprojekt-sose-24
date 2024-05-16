package org.driveractivity.mapper;

import org.driveractivity.DTO.ActivityDTO;
import org.driveractivity.DTO.ActivityGroupDTO;
import org.driveractivity.DTO.DayDTO;
import org.driveractivity.entity.Activity;
import org.driveractivity.entity.ActivityGroup;
import org.driveractivity.entity.ActivityType;
import org.driveractivity.entity.Day;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class XmlDtoToObjectMapper { //name is WIP
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public static ActivityGroup map(ActivityGroupDTO dto) {
        return ActivityGroup.builder()
                .days(mapDays(dto.getDays())).build();
    }

    public static ArrayList<Activity> mapDayToActivity(ArrayList<Day> days) {
        Iterator<Day> it = days.iterator();
        Day current = it.next();
        Day next = null;
        while(it.hasNext()) {
            next = it.next();
            if(current.getActivities().getLast().getType() == next.getActivities().getFirst().getType()) {
                current.getActivities().getLast().setDuration(Duration.between(current.getActivities().getLast().getStartTime(), next.getActivities().getFirst().getEndTime()));
                next.getActivities().remove(0);
            }
            current = next;
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
        Activity current = it.next();
        while(it.hasNext()) {
            Activity next = it.next();
            current.setDuration(Duration.between(current.getStartTime(), next.getStartTime()));
            current = next;
            if(!it.hasNext()) {
                current.setDuration(Duration.between(current.getStartTime(), LocalDateTime.of(current.getStartTime().toLocalDate(), LocalTime.of(23, 59))));
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
                .build();
    }

}
