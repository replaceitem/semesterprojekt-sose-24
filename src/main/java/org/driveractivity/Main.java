package org.driveractivity;

import org.driveractivity.entity.Activity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.driveractivity.entity.ActivityType.WORK;

public class Main {
    public static void main(String[] args) {
        Activity activity = Activity.builder()
                .type(WORK)
                .startTime(LocalDateTime.now())
                .duration(Duration.of(5, ChronoUnit.MINUTES))
                .build();
    }
}