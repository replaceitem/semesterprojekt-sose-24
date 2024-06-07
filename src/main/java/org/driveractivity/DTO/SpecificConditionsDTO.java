package org.driveractivity.DTO;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecificConditionsDTO {
    @XmlElement(name = "SpecificCondition")
    @Builder.Default
    private ArrayList<SpecificConditionDTO> specificConditions = new ArrayList<>();
}
