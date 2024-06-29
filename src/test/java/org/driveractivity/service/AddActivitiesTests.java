package org.driveractivity.service;

import org.driveractivity.entity.Activity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.driveractivity.entity.ActivityType.REST;
import static org.driveractivity.entity.ActivityType.WORK;
import static org.driveractivity.entity.CardStatus.INSERTED;

public class AddActivitiesTests {
    @Test
    public void testAddBlock() {
        DriverService driverService = DriverService.getInstance();
        Activity activity = Activity.builder()
                .type(WORK)
                .startTime(LocalDateTime.now())
                .duration(Duration.of(5, ChronoUnit.MINUTES))
                .build();
        driverService.addBlock(activity);
        assertThat(driverService.getBlocks().size()).isEqualTo(1);
        assertThat(activity.getCardStatus()).isEqualTo(INSERTED);
    }

    @Test
    public void testAddBlockWithIndex() {
        DriverService driverService = DriverService.getInstance();
        Activity activity = Activity.builder()
                .type(WORK)
                .startTime(LocalDateTime.now())
                .duration(Duration.of(5, ChronoUnit.MINUTES))
                .build();
        driverService.addBlock(activity);
        Activity activity2 = Activity.builder()
                .type(REST)
                .startTime(LocalDateTime.now())
                .duration(Duration.of(5, ChronoUnit.MINUTES))
                .build();
        driverService.addBlock(0, activity2);
        assertThat(driverService.getBlocks().size()).isEqualTo(2);
        assertThat(driverService.getBlocks().get(0).getEndTime()).isEqualTo(driverService.getBlocks().get(1).getStartTime());
        assertThat(driverService.getBlocks().get(0)).isNotEqualTo(activity);
    }
    @Test
    public void testAddBlockWithIndexException() {
        DriverService driverService = DriverService.getInstance();
        Activity activity = Activity.builder()
                .type(WORK)
                .startTime(LocalDateTime.now())
                .duration(Duration.of(5, ChronoUnit.MINUTES))
                .build();
        driverService.addBlock(activity);
        Activity activity2 = Activity.builder()
                .type(WORK)
                .startTime(LocalDateTime.now())
                .duration(Duration.of(5, ChronoUnit.MINUTES))
                .build();
        try {
            driverService.addBlock(2, activity2);
        } catch (IndexOutOfBoundsException e) {
            assertThat(e).isInstanceOf(IndexOutOfBoundsException.class);
        }
    }
    @Test
    public void testAddBlockSameType() {
        DriverService driverService = DriverService.getInstance();
        Activity activity = Activity.builder()
                .type(WORK)
                .startTime(LocalDateTime.now())
                .duration(Duration.of(5, ChronoUnit.MINUTES))
                .build();
        Activity activity2 = Activity.builder()
                .type(WORK)
                .startTime(LocalDateTime.now())
                .duration(Duration.of(5, ChronoUnit.MINUTES))
                .build();
        driverService.addBlock(activity);
        driverService.addBlock(activity2);
        assertThat(driverService.getBlocks().size()).isEqualTo(1);
        assertThat(driverService.getBlocks().getFirst().getDuration()).isEqualTo(Duration.of(10, ChronoUnit.MINUTES));
    }

    @Test
    public void addActivitySameType() {
        DriverService driverService = DriverService.getInstance();
        Activity activity = Activity.builder()
                .type(WORK)
                .startTime(LocalDateTime.now())
                .duration(Duration.of(5, ChronoUnit.MINUTES))
                .build();
        Activity activity2 = Activity.builder()
                .type(REST)
                .startTime(LocalDateTime.now())
                .duration(Duration.of(5, ChronoUnit.MINUTES))
                .build();
        Activity activity3 = Activity.builder()
                .type(WORK)
                .startTime(LocalDateTime.now())
                .duration(Duration.of(5, ChronoUnit.MINUTES))
                .build();
        Activity activity4 = Activity.builder()
                .type(WORK)
                .startTime(LocalDateTime.now())
                .duration(Duration.of(5, ChronoUnit.MINUTES))
                .build();
        driverService.addBlock(activity);
        driverService.addBlock(activity2);
        driverService.addBlock(activity3);
        driverService.addBlock(1, activity4);
        assertThat(driverService.getBlocks().size()).isEqualTo(3);
        assertThat(driverService.getBlocks().getFirst().getDuration()).isEqualTo(Duration.of(10, ChronoUnit.MINUTES));
        assertThat(driverService.getBlocks().get(1).getType()).isEqualTo(REST);
    }

    @AfterEach
    public void cleanUp() {
        DriverService.getInstance().clear();
    }
}
