import org.driveractivity.entity.Activity;
import org.driveractivity.service.DriverService;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.driveractivity.entity.ActivityType.WORK;

public class DriverServiceTest {
    @Test
    public void testAddBlock() {
        DriverService driverService = new DriverService();
        Activity activity = Activity.builder()
                .type(WORK)
                .startTime(LocalDateTime.now())
                .duration(Duration.of(5, ChronoUnit.MINUTES))
                .build();
        driverService.addBlock(activity);
        assertThat(driverService.getActivities().size()).isEqualTo(1);
    }

    @Test
    public void testAddBlockWithIndex() {
        DriverService driverService = new DriverService();
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
        driverService.addBlock(0, activity2);
        assertThat(driverService.getActivities().size()).isEqualTo(2);
        assertThat(driverService.getActivities().get(0).getEndTime()).isEqualTo(driverService.getActivities().get(1).getStartTime());
        assertThat(driverService.getActivities().get(0)).isNotEqualTo(activity);
    }
    @Test
    public void testAddBlockWithIndexException() {
        DriverService driverService = new DriverService();
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
}
