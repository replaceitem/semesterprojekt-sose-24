package org.driveractivity.entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class ActivityGroup {
    private List<Day> days;
}
