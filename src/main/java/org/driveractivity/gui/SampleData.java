package org.driveractivity.gui;

import org.driveractivity.entity.Activity;
import org.driveractivity.entity.ActivityType;
import org.driveractivity.service.DriverInterface;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class SampleData {

    private static final Random RANDOM = new Random();

    public static void populate(DriverInterface driverInterface, int count) {
        List<Duration> durations = Stream.generate(SampleData::getRandomDuration).limit(count).toList();
        Activity last = null;
        for (Duration duration : durations) {
            last = Activity.builder()
                    .type(getRandomType(last == null ? null : last.getType()))
                    .startTime(LocalDateTime.now())
                    .duration(duration)
                    .cardStatus(RANDOM.nextInt(100) < 70 ? "inserted" : "notInserted")
                    .build();
            driverInterface.addBlock(last);
        }
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
