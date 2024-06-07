package org.driveractivity.mapper;

import org.driveractivity.DTO.ActivityDTO;
import org.driveractivity.DTO.ActivityGroupDTO;
import org.driveractivity.DTO.DayDTO;
import org.driveractivity.DTO.ITFTestFileDTO;
import org.driveractivity.entity.Activity;
import org.driveractivity.entity.ActivityGroup;
import org.driveractivity.entity.Day;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ObjectToXmlDtoMapper {
    public static ITFTestFileDTO mapToXmlDto(ArrayList<Activity> activities) {
        ArrayList<Activity> splitActivities = splitActivities(activities);
        ArrayList<Day> days = mapToDays(splitActivities);
        ActivityGroup activityGroup = mapToActivityGroup(days);
        ActivityGroupDTO activityGroupDTO = mapToActivityGroupDTO(activityGroup);
        return ITFTestFileDTO.builder().activityGroup(activityGroupDTO).build();
    }

    private static ActivityGroupDTO mapToActivityGroupDTO(ActivityGroup activityGroup) {
        return ActivityGroupDTO.builder()
                .days(mapToDayDTO(activityGroup.getDays()))
                .build();
    }

    private static ArrayList<DayDTO> mapToDayDTO(ArrayList<Day> days) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        AtomicInteger atomicInteger = new AtomicInteger();
        return days.stream().map(day ->
                DayDTO.builder()
                        .activities(mapToActivityDTO(day.getActivities()))
                        .presenceCounter(atomicInteger.getAndIncrement())
                        .date(formatter.format(day.getDate()))
                        .build()
        ).collect(Collectors.toCollection(ArrayList::new));
    }

    private static ArrayList<ActivityDTO> mapToActivityDTO(ArrayList<Activity> activities) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm");
        return activities.stream().map(activity ->
                ActivityDTO.builder()
                        .activity(activity.getType().toString().toLowerCase())
                        .time(formatter.format(activity.getStartTime().toLocalTime()))
                        .slot("driver")
                        .status("single")
                        .cardStatus(activity.getCardStatus())
                        .build()
        ).collect(Collectors.toCollection(ArrayList::new));

    }

    private static ActivityGroup mapToActivityGroup(ArrayList<Day> days) {
        return ActivityGroup.builder().days(days).build();

    }

    private static ArrayList<Day> mapToDays(ArrayList<Activity> activities) {
        ArrayList<Day> days = new ArrayList<>();
        ArrayList<Activity> collectedActivities = new ArrayList<>();
        LocalDate date = activities.getFirst().getStartTime().toLocalDate();
        Day currentDay = Day.builder().date(date).build();

        for(Activity activity : activities) {
            if(!currentDay.getDate().equals(activity.getStartTime().toLocalDate())) {
                //add everything collected thus far to the current day
                currentDay.getActivities().addAll(collectedActivities);
                days.add(currentDay);
                collectedActivities.clear();

                //create next day
                currentDay = Day.builder().date(activity.getStartTime().toLocalDate()).build();
                collectedActivities.add(activity);

                
            } else {
                collectedActivities.add(activity);
            }
        }
        currentDay.setActivities(collectedActivities);
        days.add(currentDay);
        return days;
    }

    private static ArrayList<Activity> splitActivities(ArrayList<Activity> activities) {
        ArrayList<Activity> returnActivities = new ArrayList<>();
        for(Activity activity : activities) {
            LocalDate activityDate = activity.getStartTime().toLocalDate();
            returnActivities.add(activity);
            if(activity.getStartTime().plus(activity.getDuration()).toLocalDate().isAfter(activityDate)) { //if activity goes over midnight: split it into two activities
                LocalDate nextActivityDate = activity.getStartTime().plus(activity.getDuration()).toLocalDate();
                Activity nextActivity = Activity.builder()
                        .type(activity.getType())
                        .startTime(nextActivityDate.atStartOfDay())
                        .build();
                returnActivities.add(nextActivity);
            }
        }
        return returnActivities;

    }
}
