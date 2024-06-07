package org.driveractivity.entity;

import lombok.*;

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
    private String cardStatus;
    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration.toMinutes());
    }
}
