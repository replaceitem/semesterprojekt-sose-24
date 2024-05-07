package org.driveractivity.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ActivityGroupDTO {

    @XmlElement(name = "Day")
    private ArrayList<DayDTO> days;
}
