package org.driveractivity.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TimeUtil {

    /**
     * @param start (Inclusive)
     * @param end   (Exclusive)
     * @return Whether time is between start and end
     */
    public static boolean isBetween(LocalDateTime time, LocalDateTime start, LocalDateTime end) {
        return !time.isBefore(start) && time.isBefore(end);
    }

    /**
     * Maps a time in a certain range to a percentage
     * @return 0.0 when time is start, 1.0 when time is end
     */
    public static double mapInRange(LocalDateTime time, LocalDateTime start, LocalDateTime end) {
        long nanosAfterStart = start.until(time, ChronoUnit.NANOS);
        long durationNanos = start.until(end, ChronoUnit.NANOS);
        return ((double) nanosAfterStart) / durationNanos;
    }
}
