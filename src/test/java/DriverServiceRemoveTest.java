import org.driveractivity.entity.Activity;
import org.driveractivity.service.DriverService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.driveractivity.entity.ActivityType.*;

public class DriverServiceRemoveTest {

    @Test
    public void testRemoveBlock() {
        DriverService driverService = DriverService.getInstance();
        Activity activity = Activity.builder()
                .type(WORK)
                .startTime(LocalDateTime.now())
                .duration(Duration.of(5, ChronoUnit.MINUTES))
                .build();
        driverService.addBlock(activity);
        driverService.removeBlock(0);
        assertThat(driverService.getActivities().size()).isEqualTo(0);
    }

    @Test
    public void testRemoveBlockException() {
        DriverService driverService = DriverService.getInstance();
        Activity activity = Activity.builder()
                .type(WORK)
                .startTime(LocalDateTime.now())
                .duration(Duration.of(5, ChronoUnit.MINUTES))
                .build();
        driverService.addBlock(activity);
        try {
            driverService.removeBlock(1);
        } catch (IndexOutOfBoundsException e) {
            assertThat(e).isInstanceOf(IndexOutOfBoundsException.class);
        }
    }

    @Test
    public void testRemoveBlockNoMerge() {
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

        driverService.removeBlock(1);
        assertThat(driverService.getActivities().size()).isEqualTo(2); // size 2 because REST and AVAILABLE should not get merged
    }
    @Test
    public void testRemoveBlockAndMerge() {
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
        DriverService driverService = DriverService.getInstance();
        driverService.addBlock(activity);
        driverService.addBlock(activity2);
        driverService.addBlock(activity3);
        driverService.removeBlock(1);
        assertThat(driverService.getActivities().size()).isEqualTo(1); // size 1 because WORK and WORK should get merged
        assertThat(driverService.getActivities().getFirst().getDuration()).isEqualTo(Duration.of(10, ChronoUnit.MINUTES));
    }
    @Test
    public void testRemoveFirstBlock() {
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

        LocalDateTime activity2StartTime = activity2.getStartTime(); // save activity2 start time after it is added

        driverService.removeBlock(0);
        assertThat(driverService.getActivities()).doesNotContain(activity);
        assertThat(driverService.getActivities().getFirst()).isEqualTo(activity2);
        assertThat(driverService.getActivities().size()).isEqualTo(2);
        assertThat(driverService.getActivities().getFirst().getDuration()).isEqualTo(Duration.of(5, ChronoUnit.MINUTES));
        assertThat(driverService.getActivities().getLast().getDuration()).isEqualTo(Duration.of(5, ChronoUnit.MINUTES));
        assertThat(driverService.getActivities().getFirst().getStartTime()).isEqualTo(activity2StartTime); // activity2 should be first activity and its startTime should not change
    }

    @Test
    public void testRemoveLastBlock() {
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
                .duration(Duration.of(15, ChronoUnit.MINUTES))
                .build();


        DriverService driverService = DriverService.getInstance();
        driverService.addBlock(activity);
        driverService.addBlock(activity2);
        driverService.addBlock(activity3);

        driverService.removeBlock(2);
        assertThat(driverService.getActivities()).doesNotContain(activity3);
        assertThat(driverService.getActivities().size()).isEqualTo(2);
        assertThat(driverService.getActivities().getLast().getDuration()).isEqualTo(Duration.of(5, ChronoUnit.MINUTES));

    }



    @AfterEach
    public void cleanUp() {
        DriverService.getInstance().clear();
    }

}
