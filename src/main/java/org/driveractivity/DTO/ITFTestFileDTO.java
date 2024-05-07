package org.driveractivity.DTO;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ITFTestFile")
public class ITFTestFileDTO {
    @XmlElement(name = "Activities")
    private ActivityGroupDTO activityGroup;
}
