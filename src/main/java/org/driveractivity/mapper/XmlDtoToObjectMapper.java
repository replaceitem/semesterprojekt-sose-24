package org.driveractivity.mapper;

import javafx.util.converter.LocalDateStringConverter;
import org.driveractivity.DTO.ActivityDTO;
import org.driveractivity.DTO.ActivityGroupDTO;
import org.driveractivity.DTO.DayDTO;
import org.driveractivity.entity.Activity;
import org.driveractivity.entity.ActivityGroup;
import org.driveractivity.entity.ActivityType;
import org.driveractivity.entity.Day;

import javax.swing.text.DateFormatter;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class XmlDtoToObjectMapper { //name is WIP
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public static ActivityGroup map(ActivityGroupDTO dto) {
        return ActivityGroup.builder()
                .days(mapDays(dto.getDays())).build();
    }
    private static List<Day> mapDays(List<DayDTO> days) {
        return days.stream().map(XmlDtoToObjectMapper::mapDay).toList();
    }
    private static Day mapDay(DayDTO dayDTO) {
        return Day.builder()
                .date(LocalDate.parse(dayDTO.getDate(), formatter))
                .presenceCounter(dayDTO.getPresenceCounter())
                .distance(dayDTO.getDistance())
                .activities(mapActivities(dayDTO.getActivities(), LocalDate.parse(dayDTO.getDate(), formatter)))
                .build();
    }
    private static List<Activity> mapActivities(List<ActivityDTO> activities, LocalDate date) {

        return activities.stream().map(x -> mapActivity(x, date)).toList();
    }
    private static Activity mapActivity(ActivityDTO activityDTO, LocalDate date) {
        return Activity.builder()
                .type(ActivityType.valueOf(activityDTO.getActivity().toUpperCase()))
                .startTime(LocalDateTime.of(date, LocalTime.parse(activityDTO.getTime())))
                .build();
    }

}
