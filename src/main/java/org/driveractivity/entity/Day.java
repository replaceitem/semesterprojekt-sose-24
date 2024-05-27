package org.driveractivity.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;

@Data
@Builder(toBuilder = true)
public class Day {
    private LocalDate date;
    private long presenceCounter;
    private long distance;
    @Builder.Default
    private ArrayList<Activity> activities = new ArrayList<>();
}
