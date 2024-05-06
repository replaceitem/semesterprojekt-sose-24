package org.driveractivity.gui;

import org.driveractivity.entity.Activity;
import org.driveractivity.entity.ActivityType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class SampleData {

    private static final Random RANDOM = new Random();
    
    public static List<Activity> getSampleData(int size) {
        List<Duration> durations = Stream.generate(SampleData::getRandomDuration).limit(size).toList();
        ArrayList<Activity> activities = new ArrayList<>();
        Activity last = null;
        for (Duration duration : durations) {
            last = new Activity(getRandomType(last == null ? null : last.getType()), duration, last == null ? LocalDateTime.now() : last.getEndTime());
            activities.add(last);
        }
        return activities;
    }
    
    public static Duration getRandomDuration() {
        int i = RANDOM.nextInt(2);
        return switch (i) {
            case 0 -> Duration.ofMinutes(RANDOM.nextLong(60));
            case 1 -> Duration.ofHours(RANDOM.nextLong(50)).plusMinutes(RANDOM.nextLong(60));
            default -> throw new RuntimeException();
        };
    }
    
    public static ActivityType getRandomType(ActivityType except) {
        List<ActivityType> list = Arrays.stream(ActivityType.values()).filter(activityType -> activityType != except).toList();
        return list.get(RANDOM.nextInt(list.size()));
    }
}
