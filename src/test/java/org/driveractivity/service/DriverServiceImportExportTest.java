package org.driveractivity.service;

import org.driveractivity.entity.Activity;
import org.driveractivity.entity.SpecificCondition;
import org.driveractivity.entity.SpecificConditionType;
import org.driveractivity.exception.FileImportException;
import org.driveractivity.exception.SpecificConditionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.driveractivity.entity.ActivityType.WORK;

public class DriverServiceImportExportTest {
    @Test
    public void importFrom() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("Beispiel2.xml")).getFile());
        DriverService driverService = DriverService.getInstance();
        driverService.importFrom(file);
        ArrayList<Activity> activities = driverService.getBlocks();
        assertThat(activities.size()).isEqualTo(4); //xml contains 5 activities, but 1 spans over 2 days, so it is merged
        assertThat(activities.getFirst().getDuration()).isEqualTo(Duration.of(1, ChronoUnit.HOURS));
        assertThat(driverService.getSpecificConditions()).isNotEmpty();
    }

    @Test
    public void outputTo() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("Beispiel2.xml")).getFile());
        DriverService driverService = DriverService.getInstance();
        driverService.importFrom(file);
        assertThat(driverService.getActivities()).isNotNull();
        driverService.exportToXML(new File("output.xml"));
    }
    @Test
    public void specificConditionTest() throws FileImportException, SpecificConditionException {
        DriverService driverService = DriverService.getInstance();
        Activity activity = Activity.builder()
                .type(WORK)
                .startTime(LocalDateTime.now())
                .duration(Duration.of(5, ChronoUnit.MINUTES))
                .cardStatus("inserted")
                .build();
        driverService.addBlock(activity);
        driverService.addSpecificCondition(List.of(SpecificCondition.builder().timestamp(LocalDateTime.now()).specificConditionType(SpecificConditionType.BEGIN_FT).build()));

        File file = new File("output.xml");
        driverService.exportToXML(file);
        driverService.clear();
        driverService.importFrom(file);
        assertThat(driverService.getSpecificConditions().size()).isEqualTo(1);
        assertThat(driverService.getSpecificConditions().getFirst().getSpecificConditionType()).isEqualTo(SpecificConditionType.BEGIN_FT);
    }

    @Test
    public void roundTripTest() throws FileImportException {
        //Setup of round trip: import from file, export to file, import from file again
        //Step 1: Import from file
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("Beispiel2.xml")).getFile());
        DriverService driverService = DriverService.getInstance();
        driverService.importFrom(file);
        //Save activities and specific conditions to compare after round trip
        ArrayList<Activity> savedActivities = driverService.getActivities();
        ArrayList<SpecificCondition> savedSpecificConditions = driverService.getSpecificConditions();

        //Step 2: Export to file
        File file2 = new File("output.xml");
        driverService.exportToXML(file2);
        driverService.clear();
        //Step 3: Import from file again
        driverService.importFrom(file2);
        //Check if activities and specific conditions are the same as before the round trip
        assertThat(driverService.getActivities()).isEqualTo(savedActivities);
        assertThat(driverService.getSpecificConditions()).isEqualTo(savedSpecificConditions);
    }

    @AfterEach
    public void cleanUp() {
        DriverService.getInstance().clear();
    }

}
