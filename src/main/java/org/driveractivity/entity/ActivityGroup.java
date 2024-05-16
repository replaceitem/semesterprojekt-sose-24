package org.driveractivity.entity;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

@Data
@Builder(toBuilder = true)
public class ActivityGroup {
    private ArrayList<Day> days;
}
