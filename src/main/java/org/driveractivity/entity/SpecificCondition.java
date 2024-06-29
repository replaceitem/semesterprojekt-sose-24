package org.driveractivity.entity;

import lombok.*;

import java.time.LocalDateTime;
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class SpecificCondition {
    @NonNull
    private LocalDateTime timestamp;
    @NonNull
    private SpecificConditionType specificConditionType;
    @Builder.Default
    private boolean isWithoutEnd = false;

}
