package org.driveractivity.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class Day {
    private LocalDate date;
    private long presenceCounter;
    private long distance;
    private List<Activity> activities;
}
