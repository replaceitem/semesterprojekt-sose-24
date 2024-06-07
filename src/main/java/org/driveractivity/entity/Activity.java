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
    @Builder.Default
    private String cardStatus = "inserted";
    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration.toMinutes());
    }
    public boolean canMergeWith(Activity activity) {
        return this.type == activity.type && this.cardStatus.equals(activity.cardStatus);
    }
}
