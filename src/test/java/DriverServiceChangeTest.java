import org.driveractivity.entity.Activity;
import org.driveractivity.service.DriverService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.driveractivity.entity.ActivityType.*;
import static org.driveractivity.entity.ActivityType.DRIVING;

public class DriverServiceChangeTest {

    @Test
    public void changeActivityShortenEndTime() {
        Activity activity = Activity.builder()
                .type(WORK)
                .startTime(LocalDateTime.now())
                .duration(Duration.of(5, ChronoUnit.MINUTES))
                .build();
        Activity activity2 = Activity.builder()
                .type(REST)
                .startTime(LocalDateTime.now())
                .duration(Duration.of(10, ChronoUnit.MINUTES))
                .build();
        Activity activity3 = Activity.builder()
                .type(AVAILABLE)
                .startTime(LocalDateTime.now())
                .duration(Duration.of(5, ChronoUnit.MINUTES))
                .build();

        DriverService driverService = DriverService.getInstance();
        driverService.addBlock(activity);
        driverService.addBlock(activity2);
        driverService.addBlock(activity3);

        LocalDateTime activity3EndTime = activity3.getEndTime(); // save activity3 end time - should change after changeBlock

        Activity changedActivity = Activity.builder()
                .type(DRIVING)
                .startTime(LocalDateTime.now())
                .duration(Duration.of(2, ChronoUnit.MINUTES))
                .build();

        driverService.changeBlock(1, changedActivity);

        assertThat(driverService.getActivities().size()).isEqualTo(3);
        assertThat(driverService.getActivities().getLast().getEndTime()).isBefore(activity3EndTime);
    }

    @Test
    public void changeActivityExtendEndTime() {
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
                .type(AVAILABLE)
                .startTime(LocalDateTime.now())
                .duration(Duration.of(5, ChronoUnit.MINUTES))
                .build();

        DriverService driverService = DriverService.getInstance();
        driverService.addBlock(activity);
        driverService.addBlock(activity2);
        driverService.addBlock(activity3);

        LocalDateTime activity3EndTime = activity3.getEndTime(); // save activity3 end time - should change after changing activity

        Activity changedActivity = Activity.builder()
                .type(DRIVING)
                .startTime(LocalDateTime.now())
                .duration(Duration.of(15, ChronoUnit.MINUTES))
                .build();

        driverService.changeBlock(1, changedActivity);

        assertThat(driverService.getActivities().size()).isEqualTo(3);
        assertThat(driverService.getActivities().getLast().getEndTime()).isAfter(activity3EndTime);
    }

    @Test
    public void changeSingleActivity() {
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

        DriverService driverService = DriverService.getInstance();
        driverService.addBlock(activity);
        driverService.addBlock(activity2);

        Activity newActivity = Activity.builder()
                .type(DRIVING)
                .startTime(LocalDateTime.now())
                .duration(Duration.of(15, ChronoUnit.MINUTES))
                .build();

        driverService.changeBlock(1, newActivity);
        assertThat(driverService.getActivities().get(1).getDuration()).isEqualTo(Duration.of(15, ChronoUnit.MINUTES));
        assertThat(driverService.getActivities().get(1).getType()).isEqualTo(DRIVING);
    }

    @AfterEach
    public void cleanUp() {
        DriverService.getInstance().clear();
    }

}
