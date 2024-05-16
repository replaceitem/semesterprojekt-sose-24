package org.driveractivity.DTO;

import lombok.Data;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
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
