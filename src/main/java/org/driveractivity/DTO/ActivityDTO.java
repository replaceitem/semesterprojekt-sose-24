package org.driveractivity.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
