package org.driveractivity.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ITFTestFile")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ITFTestFileDTO {
    @XmlElement(name = "Activities")
    private ActivityGroupDTO activityGroup;
    @XmlElement(name = "SpecificConditions")
    SpecificConditionsDTO specificConditionsDTO;
}
