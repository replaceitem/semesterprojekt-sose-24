package org.driveractivity.mapper;

import org.driveractivity.DTO.*;
import org.driveractivity.entity.Activity;
import org.driveractivity.entity.ActivityGroup;
import org.driveractivity.entity.Day;
import org.driveractivity.entity.SpecificCondition;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ObjectToXmlDtoMapper {
    public static ITFTestFileDTO mapToXmlDto(ArrayList<Activity> activities, ArrayList<SpecificCondition> specificConditions) {
        ArrayList<Activity> splitActivities = splitActivities(activities);
        ArrayList<Day> days = mapToDays(splitActivities);
        ActivityGroup activityGroup = mapToActivityGroup(days);
        ActivityGroupDTO activityGroupDTO = mapToActivityGroupDTO(activityGroup);
        SpecificConditionsDTO specificConditionsDTO = SpecificConditionsDTO.builder().specificConditions(mapToSpecificConditionDTO(specificConditions)).build();
        return ITFTestFileDTO.builder()
                .activityGroup(activityGroupDTO)
                .specificConditionsDTO(specificConditionsDTO)
                .build();
    }

    private static ArrayList<SpecificConditionDTO> mapToSpecificConditionDTO(ArrayList<SpecificCondition> specificConditions) {
        return specificConditions.stream().map(specificCondition ->
                SpecificConditionDTO.builder()
                        .timestamp(specificCondition.getTimestamp().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))
                        .specificConditionType(specificCondition.getSpecificConditionType().mapNameToString())
                        .build()
        ).collect(Collectors.toCollection(ArrayList::new));
    }

    private static ActivityGroupDTO mapToActivityGroupDTO(ActivityGroup activityGroup) {
        return ActivityGroupDTO.builder()
                .days(mapToDayDTO(activityGroup.getDays()))
                .build();
    }

    private static ArrayList<DayDTO> mapToDayDTO(ArrayList<Day> days) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        AtomicInteger atomicInteger = new AtomicInteger(1);
        return days.stream().map(day ->
                DayDTO.builder()
                        .activities(mapToActivityDTO(day.getActivities()))
                        .presenceCounter(atomicInteger.getAndIncrement())
                        .date(formatter.format(day.getDate()))
                        .build()
        ).collect(Collectors.toCollection(ArrayList::new));
    }

    private static ArrayList<ActivityDTO> mapToActivityDTO(ArrayList<Activity> activities) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return activities.stream().map(activity ->
                ActivityDTO.builder()
                        .activity(activity.getType().toString().toLowerCase())
                        .time(formatter.format(activity.getStartTime().toLocalTime()))
                        .slot("driver")
                        .status("single")
                        .cardStatus(activity.getCardStatus().mapNameToString())
                        .build()
        ).collect(Collectors.toCollection(ArrayList::new));

    }

    private static ActivityGroup mapToActivityGroup(ArrayList<Day> days) {
        return ActivityGroup.builder().days(days).build();

    }

    private static ArrayList<Day> mapToDays(ArrayList<Activity> activities) {
        ArrayList<Day> days = new ArrayList<>();
        ArrayList<Activity> collectedActivities = new ArrayList<>();
        if(activities.isEmpty()) {
            return days;
        }
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
            returnActivities.add(activity);

            LocalDate startDate = activity.getStartTime().toLocalDate();
            LocalDate firstNewDate = startDate.plusDays(1);
            LocalDateTime endTime = activity.getStartTime().plus(activity.getDuration());
            // create a new activity at the start of each day which overlaps the activity
            firstNewDate.datesUntil(endTime.toLocalDate().plusDays(1))
                    .map(LocalDate::atStartOfDay)
                    .filter(localDateTime -> localDateTime.isBefore(endTime))
                    .map(activity::withStartTime)
                    .forEach(returnActivities::add);
        }
        return returnActivities;

    }
}
