package org.driveractivity.DTO;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.driveractivity.entity.Activity;

import javax.xml.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

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
    private List<ActivityDTO> activities;
}
