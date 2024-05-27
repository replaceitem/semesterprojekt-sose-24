import org.driveractivity.entity.Activity;
import org.driveractivity.service.DriverService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.driveractivity.entity.ActivityType.*;

public class DriverServiceTest {
    @Test
    public void testAddBlock() {
        DriverService driverService = DriverService.getInstance();
        Activity activity = Activity.builder()
                .type(WORK)
                .startTime(LocalDateTime.now())
                .duration(Duration.of(5, ChronoUnit.MINUTES))
                .build();
        driverService.addBlock(activity);
        assertThat(driverService.getActivities().size()).isEqualTo(1);
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
        assertThat(driverService.getActivities().size()).isEqualTo(1);
        assertThat(driverService.getActivities().getFirst().getDuration()).isEqualTo(Duration.of(10, ChronoUnit.MINUTES));
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
        assertThat(driverService.getActivities().size()).isEqualTo(3);
        assertThat(driverService.getActivities().getFirst().getDuration()).isEqualTo(Duration.of(10, ChronoUnit.MINUTES));
        assertThat(driverService.getActivities().get(1).getType()).isEqualTo(REST);
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
        assertThat(driverService.getActivities().size()).isEqualTo(2);
        assertThat(driverService.getActivities().get(0).getEndTime()).isEqualTo(driverService.getActivities().get(1).getStartTime());
        assertThat(driverService.getActivities().get(0)).isNotEqualTo(activity);
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
    public void importFrom() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("Beispiel2.xml")).getFile());
        DriverService driverService = DriverService.getInstance();
        ArrayList<Activity> activities = new ArrayList<>(driverService.importFrom(file));
        assertThat(activities.size()).isEqualTo(4); //xml contains 5 activities, but 1 spans over 2 days, so it is merged
        assertThat(activities.getFirst().getDuration()).isEqualTo(Duration.of(1, ChronoUnit.HOURS));
    }

    @Test
    public void outputTo() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("Beispiel2.xml")).getFile());
        DriverService driverService = DriverService.getInstance();
        driverService.importFrom(file);
        assertThat(driverService.getActivities()).isNotNull();
        driverService.exportToXML();
    }

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

    @Test
    public void changeSingleActivityMergeAfter() {
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
                .type(DRIVING)
                .startTime(LocalDateTime.now())
                .duration(Duration.of(5, ChronoUnit.MINUTES))
                .build();

        DriverService driverService = DriverService.getInstance();
        driverService.addBlock(activity);
        driverService.addBlock(activity2);
        driverService.addBlock(activity3);

        Activity newActivity = Activity.builder()
                .type(DRIVING)
                .startTime(LocalDateTime.now())
                .duration(Duration.of(15, ChronoUnit.MINUTES))
                .build();

        driverService.changeBlock(1, newActivity);
        assertThat(driverService.getActivities().get(1).getDuration()).isEqualTo(Duration.of(20, ChronoUnit.MINUTES));
        assertThat(driverService.getActivities().get(1).getType()).isEqualTo(DRIVING);
        assertThat(driverService.getActivities().size()).isEqualTo(2);
    }

    @Test
    public void changeSingleActivityMergeBefore() {
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
                .type(DRIVING)
                .startTime(LocalDateTime.now())
                .duration(Duration.of(5, ChronoUnit.MINUTES))
                .build();

        DriverService driverService = DriverService.getInstance();
        driverService.addBlock(activity);
        driverService.addBlock(activity2);
        driverService.addBlock(activity3);

        LocalDateTime activity3EndTime = activity3.getEndTime(); // save activity3 end time - should not change after merge

        Activity newActivity = Activity.builder()
                .type(WORK)
                .startTime(LocalDateTime.now())
                .duration(activity2.getDuration())
                .build();

        driverService.changeBlock(1, newActivity);
        assertThat(driverService.getActivities().getFirst().getDuration()).isEqualTo(Duration.of(10, ChronoUnit.MINUTES));
        assertThat(driverService.getActivities().getFirst().getType()).isEqualTo(WORK);
        assertThat(driverService.getActivities().size()).isEqualTo(2);
        assertThat(driverService.getActivities().getLast().getEndTime()).isEqualTo(activity3EndTime); //end time of activity3 should not change
    }

    @Test
    public void mergeThreeActivities() {
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

        Activity changedActivity = Activity.builder()
                .type(WORK)
                .startTime(LocalDateTime.now())
                .duration(Duration.of(5, ChronoUnit.MINUTES))
                .build();

        driverService.changeBlock(1, changedActivity);

        assertThat(driverService.getActivities().size()).isEqualTo(1);
        assertThat(driverService.getActivities().getFirst().getDuration()).isEqualTo(Duration.of(15, ChronoUnit.MINUTES));
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

        LocalDateTime activity3EndTime = activity3.getEndTime(); // save activity3 end time - should change after merge

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

        LocalDateTime activity3EndTime = activity3.getEndTime(); // save activity3 end time - should change after merge

        Activity changedActivity = Activity.builder()
                .type(DRIVING)
                .startTime(LocalDateTime.now())
                .duration(Duration.of(2, ChronoUnit.MINUTES))
                .build();

        driverService.changeBlock(1, changedActivity);

        assertThat(driverService.getActivities().size()).isEqualTo(3);
        assertThat(driverService.getActivities().getLast().getEndTime()).isBefore(activity3EndTime);
    }

    @AfterEach
    public void cleanUp() {
        DriverService.getInstance().getActivities().clear();
    }
}

