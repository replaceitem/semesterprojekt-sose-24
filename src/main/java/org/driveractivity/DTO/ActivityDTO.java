package org.driveractivity.DTO;

import lombok.Builder;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.time.LocalDateTime;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ActivityDTO {
    @XmlAttribute(name = "time")
    private String time;
    @XmlAttribute(name = "activity")
    private String activity;
    @XmlAttribute(name = "slot")
    private String slot;
    @XmlAttribute(name = "status")
    private String status;
    @XmlAttribute(name = "cardStatus")
    private String cardStatus;
}
