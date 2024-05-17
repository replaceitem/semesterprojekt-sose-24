package org.driveractivity.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class Activity {
    @NonNull
    private ActivityType type;
    private Duration duration;
    private LocalDateTime startTime;

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration.toMinutes());
    }
}
