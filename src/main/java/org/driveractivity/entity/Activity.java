package org.driveractivity.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class Activity {
    @NonNull
    private ActivityType type;
    private Duration duration;
    private LocalDateTime startTime;

    public LocalDateTime getEndTime() {
        return startTime.plus(duration.toMinutes(), ChronoUnit.MINUTES);
    }
}
