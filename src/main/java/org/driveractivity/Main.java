package org.driveractivity;

import org.driveractivity.DTO.ITFTestFileDTO;
import org.driveractivity.entity.Activity;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.driveractivity.entity.ActivityType.WORK;

public class Main {
    public static void main(String[] args) {
        ClassLoader classLoader = Main.class.getClassLoader();
        Activity activity = Activity.builder()
                .type(WORK)
                .startTime(LocalDateTime.now())
                .duration(Duration.of(5, ChronoUnit.MINUTES))
                .build();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ITFTestFileDTO.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            File f = new File(classLoader.getResource("Beispiel1.xml").getFile());
            ITFTestFileDTO itfTestFileDTO = (ITFTestFileDTO) unmarshaller.unmarshal(f);
            System.out.println(itfTestFileDTO);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

    }
}