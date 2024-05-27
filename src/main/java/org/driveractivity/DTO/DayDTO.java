package org.driveractivity.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DayDTO {
    @XmlAttribute(name = "date")
    private String date;
    @XmlAttribute(name = "presenceCounter")
    private long presenceCounter;
    @XmlAttribute(name = "distance")
    private long distance;
    @XmlElement(name = "Activity")
    private ArrayList<ActivityDTO> activities;
}
