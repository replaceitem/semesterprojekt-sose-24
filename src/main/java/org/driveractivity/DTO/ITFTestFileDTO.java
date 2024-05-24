package org.driveractivity.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ITFTestFile")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ITFTestFileDTO {
    @XmlElement(name = "Activities")
    private ActivityGroupDTO activityGroup;
}
