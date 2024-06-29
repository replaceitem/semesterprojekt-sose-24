package org.driveractivity.service;

import org.driveractivity.entity.Activity;
import org.driveractivity.entity.ActivityType;
import org.driveractivity.entity.SpecificCondition;
import org.driveractivity.entity.SpecificConditionType;
import org.driveractivity.exception.SpecificConditionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SpecificConditionTests {
    @Test
    public void testAddOutOfScopeSpecificCondition() throws SpecificConditionException {
        DriverService driverService = DriverService.getInstance();
        ArrayList<SpecificCondition> specificConditions = new ArrayList<>();
        SpecificCondition outOfScopeBegin = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.BEGIN_OUT_OF_SCOPE)
                .timestamp(LocalDateTime.now())
                .build();
        SpecificCondition outOfScopeEnd = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.END_OUT_OF_SCOPE)
                .timestamp(LocalDateTime.now().plusMinutes(15))
                .build();
        specificConditions.add(outOfScopeBegin);
        specificConditions.add(outOfScopeEnd);
        driverService.addSpecificCondition(specificConditions);
        //this should work fine, so assert that they are in the list
        assertThat(driverService.getSpecificConditions()).contains(outOfScopeBegin, outOfScopeEnd);
    }

    @Test
    public void testAddIntersectingOutOfScope() throws SpecificConditionException {
        DriverService driverService = DriverService.getInstance();
        ArrayList<SpecificCondition> specificConditions = new ArrayList<>();
        SpecificCondition outOfScopeBegin = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.BEGIN_OUT_OF_SCOPE)
                .timestamp(LocalDateTime.now())
                .build();
        SpecificCondition outOfScopeEnd = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.END_OUT_OF_SCOPE)
                .timestamp(LocalDateTime.now().plusMinutes(15))
                .build();
        specificConditions.add(outOfScopeBegin);
        specificConditions.add(outOfScopeEnd);
        driverService.addSpecificCondition(specificConditions);
        //this should work fine, so assert that they are in the list
        assertThat(driverService.getSpecificConditions()).contains(outOfScopeBegin, outOfScopeEnd);

        SpecificCondition outOfScopeBegin2 = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.BEGIN_OUT_OF_SCOPE)
                .timestamp(LocalDateTime.now())
                .build();
        SpecificCondition outOfScopeEnd2 = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.END_OUT_OF_SCOPE)
                .timestamp(LocalDateTime.now().plusMinutes(5))
                .build();
        specificConditions.clear();

        specificConditions.add(outOfScopeBegin2);
        specificConditions.add(outOfScopeEnd2);
        assertThatThrownBy(() -> driverService.addSpecificCondition(specificConditions))
                .isInstanceOf(SpecificConditionException.class)
                .hasMessage("The new OUT_OF_SCOPE conditions intersect with existing ones");

    }
    @Test
    public void testAddIncompleteOutOfScope() {
        DriverService driverService = DriverService.getInstance();
        ArrayList<SpecificCondition> specificConditions = new ArrayList<>();
        SpecificCondition outOfScopeBegin = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.BEGIN_OUT_OF_SCOPE)
                .timestamp(LocalDateTime.now())
                .build();
        SpecificCondition outOfScopeEnd = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.END_OUT_OF_SCOPE)
                .timestamp(LocalDateTime.now().minusMinutes(15))
                .build();
        specificConditions.add(outOfScopeBegin);
        specificConditions.add(outOfScopeEnd);
        assertThatThrownBy(() -> driverService.addSpecificCondition(specificConditions))
                .isInstanceOf(SpecificConditionException.class)
                .hasMessage("The BEGIN_FT or BEGIN_OUT_OF_SCOPE must take place before the END_FT or END_OUT_OF_SCOPE");

    }
    @Test
    public void testAddBeginTakesPlaceAfterEnd() {
        DriverService driverService = DriverService.getInstance();
        ArrayList<SpecificCondition> specificConditions = new ArrayList<>();
        SpecificCondition outOfScopeBegin = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.BEGIN_OUT_OF_SCOPE)
                .timestamp(LocalDateTime.now())
                .build();
        specificConditions.add(outOfScopeBegin);

        assertThatThrownBy(() -> driverService.addSpecificCondition(specificConditions))
                .isInstanceOf(SpecificConditionException.class)
                .hasMessage("If a BEGIN_OUT_OF_SCOPE is added, an END_OUT_OF_SCOPE must be added as well");

    }

    @Test
    public void testAddFullFTSpecificCondition() throws SpecificConditionException {
        DriverService driverService = DriverService.getInstance();
        ArrayList<SpecificCondition> specificConditions = new ArrayList<>();
        SpecificCondition beginFT = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.BEGIN_FT)
                .timestamp(LocalDateTime.now())
                .build();
        SpecificCondition endFT = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.END_FT)
                .timestamp(LocalDateTime.now().plusMinutes(15))
                .build();
        specificConditions.add(beginFT);
        specificConditions.add(endFT);
        driverService.addSpecificCondition(specificConditions);
        //this should work fine, so assert that they are in the list
        assertThat(driverService.getSpecificConditions()).contains(beginFT, endFT);
    }

    @Test
    public void testAddBeginFTWithoutEndEndsWithDrivingActivity() throws SpecificConditionException {
        DriverService driverService = DriverService.getInstance();
        ArrayList<SpecificCondition> specificConditions = new ArrayList<>();
        SpecificCondition beginFT = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.BEGIN_FT)
                .timestamp(LocalDateTime.now())
                .build();
        specificConditions.add(beginFT);

        Activity restActivity = Activity.builder()
                .type(ActivityType.REST)
                .startTime(LocalDateTime.now())
                .duration(Duration.of(20, ChronoUnit.MINUTES))
                .build();
        Activity drivingActivity = Activity.builder()
                .type(ActivityType.DRIVING)
                .duration(Duration.of(20, ChronoUnit.MINUTES))
                .build();

        driverService.addBlock(restActivity);
        driverService.addBlock(drivingActivity);


        driverService.addSpecificCondition(specificConditions);
        //this should work fine, so assert that they are in the list
        assertThat(driverService.getSpecificConditions().size()).isEqualTo(2);
        assertThat(driverService.getSpecificConditions().getFirst().isWithoutEnd()).isFalse();
        assertThat(driverService.getSpecificConditions().getLast().getTimestamp()).isEqualTo(drivingActivity.getStartTime());
    }

    @Test
    public void testAddOnlyBeginFT() throws SpecificConditionException {
        DriverService driverService = DriverService.getInstance();
        ArrayList<SpecificCondition> specificConditions = new ArrayList<>();
        SpecificCondition beginFT = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.BEGIN_FT)
                .timestamp(LocalDateTime.now())
                .build();
        specificConditions.add(beginFT);
        driverService.addSpecificCondition(specificConditions);
        //this should work fine, so assert that they are in the list
        assertThat(driverService.getSpecificConditions()).contains(beginFT);
        assertThat(driverService.getSpecificConditions().getFirst().isWithoutEnd()).isTrue();
    }

    @Test
    public void testAddOnlyEndFT() {
        DriverService driverService = DriverService.getInstance();
        ArrayList<SpecificCondition> specificConditions = new ArrayList<>();
        SpecificCondition endFT = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.END_FT)
                .timestamp(LocalDateTime.now())
                .build();
        specificConditions.add(endFT);
        assertThatThrownBy(() -> driverService.addSpecificCondition(specificConditions))
                .isInstanceOf(SpecificConditionException.class)
                .hasMessage("If an END_FT is added, a BEGIN_FT must either already exist or be added before it.");
    }

    @Test
    public void testAddOnlyEndFTBeforeOtherFTs() throws SpecificConditionException {
        DriverService driverService = DriverService.getInstance();
        ArrayList<SpecificCondition> specificConditions = new ArrayList<>();

        SpecificCondition beginFT = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.BEGIN_FT)
                .timestamp(LocalDateTime.now())
                .build();
        SpecificCondition endFT = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.END_FT)
                .timestamp(LocalDateTime.now().plusMinutes(15))
                .build();

        specificConditions.add(beginFT);
        specificConditions.add(endFT);

        driverService.addSpecificCondition(specificConditions);

        specificConditions.clear();

        SpecificCondition endFT2 = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.END_FT)
                .timestamp(LocalDateTime.now().plusMinutes(20))
                .build();
        specificConditions.add(endFT2);
        assertThatThrownBy(() -> driverService.addSpecificCondition(specificConditions))
                .isInstanceOf(SpecificConditionException.class)
                .hasMessage("If an END_FT is added, a BEGIN_FT must either already exist or be added before it.");
    }

    @Test
    public void testAddIntersectingFT() throws SpecificConditionException {
        DriverService driverService = DriverService.getInstance();
        ArrayList<SpecificCondition> specificConditions = new ArrayList<>();
        SpecificCondition beginFT = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.BEGIN_FT)
                .timestamp(LocalDateTime.now())
                .build();
        SpecificCondition endFT = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.END_FT)
                .timestamp(LocalDateTime.now().plusMinutes(15))
                .build();
        specificConditions.add(beginFT);
        specificConditions.add(endFT);
        driverService.addSpecificCondition(specificConditions);
        //this should work fine, so assert that they are in the list
        assertThat(driverService.getSpecificConditions()).contains(beginFT, endFT);

        SpecificCondition beginFT2 = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.BEGIN_FT)
                .timestamp(LocalDateTime.now())
                .build();
        SpecificCondition endFT2 = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.END_FT)
                .timestamp(LocalDateTime.now().plusMinutes(5))
                .build();
        specificConditions.clear();

        specificConditions.add(beginFT2);
        specificConditions.add(endFT2);
        assertThatThrownBy(() -> driverService.addSpecificCondition(specificConditions))
                .isInstanceOf(SpecificConditionException.class)
                .hasMessage("The new FT conditions intersect with existing ones");

    }

    @Test
    public void testAddFTAfterUnclosedFT() throws SpecificConditionException {
        DriverService driverService = DriverService.getInstance();
        ArrayList<SpecificCondition> specificConditions = new ArrayList<>();
        SpecificCondition beginFT = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.BEGIN_FT)
                .timestamp(LocalDateTime.now().minusMinutes(100))
                .build();
        specificConditions.add(beginFT);
        driverService.addSpecificCondition(specificConditions);
        //this should work fine, so assert that they are in the list
        assertThat(driverService.getSpecificConditions()).contains(beginFT);
        assertThat(driverService.getSpecificConditions().getFirst().isWithoutEnd()).isTrue();

        SpecificCondition beginFT2 = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.BEGIN_FT)
                .timestamp(LocalDateTime.now())
                .build();
        SpecificCondition endFT2 = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.END_FT)
                .timestamp(LocalDateTime.now().plusMinutes(15))
                .build();

        specificConditions.clear();
        specificConditions.add(beginFT2);
        specificConditions.add(endFT2);

        assertThatThrownBy(() -> driverService.addSpecificCondition(specificConditions))
                .isInstanceOf(SpecificConditionException.class)
                .hasMessage("Please close the unclosed FT before adding further FT conditions after it.");
    }

    @Test
    public void testFixUnclosedFTAndAddFurtherFT() throws SpecificConditionException {
        DriverService driverService = DriverService.getInstance();
        ArrayList<SpecificCondition> specificConditions = new ArrayList<>();
        SpecificCondition beginFT = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.BEGIN_FT)
                .timestamp(LocalDateTime.now().minusMinutes(100))
                .build();
        SpecificCondition endFT = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.END_FT)
                .timestamp(LocalDateTime.now().minusMinutes(50))
                .build();
        specificConditions.add(beginFT);
        driverService.addSpecificCondition(specificConditions);
        //this should work fine, so assert that they are in the list
        assertThat(driverService.getSpecificConditions()).contains(beginFT);
        assertThat(driverService.getSpecificConditions().getFirst().isWithoutEnd()).isTrue();


        specificConditions.clear();
        specificConditions.add(endFT);
        driverService.addSpecificCondition(specificConditions);
        assertThat(driverService.getSpecificConditions()).contains(beginFT, endFT);
        assertThat(driverService.getSpecificConditions().getFirst().isWithoutEnd()).isFalse();



        SpecificCondition beginFT2 = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.BEGIN_FT)
                .timestamp(LocalDateTime.now())
                .build();
        SpecificCondition endFT2 = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.END_FT)
                .timestamp(LocalDateTime.now().plusMinutes(15))
                .build();

        specificConditions.clear();
        specificConditions.add(beginFT2);
        specificConditions.add(endFT2);

        driverService.addSpecificCondition(specificConditions);
        assertThat(driverService.getSpecificConditions()).contains(beginFT, endFT, beginFT2, endFT2);
    }

    @Test
    public void testRemoveSpecificConditionsRemoveStartFT() throws SpecificConditionException {
        DriverService driverService = DriverService.getInstance();
        ArrayList<SpecificCondition> specificConditions = new ArrayList<>();
        SpecificCondition beginFT = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.BEGIN_FT)
                .timestamp(LocalDateTime.now().minusMinutes(100))
                .build();
        SpecificCondition endFT = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.END_FT)
                .timestamp(LocalDateTime.now().minusMinutes(50))
                .build();
        specificConditions.add(beginFT);
        specificConditions.add(endFT);

        driverService.addSpecificCondition(specificConditions);

        specificConditions.clear();

        specificConditions.add(beginFT);

        driverService.removeSpecificCondition(beginFT);

        assertThat(driverService.getSpecificConditions()).isEmpty();

    }

    @Test
    public void testRemoveSpecificConditionsRemoveEndFT() throws SpecificConditionException {
        DriverService driverService = DriverService.getInstance();
        ArrayList<SpecificCondition> specificConditions = new ArrayList<>();
        SpecificCondition beginFT = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.BEGIN_FT)
                .timestamp(LocalDateTime.now().minusMinutes(100))
                .build();
        SpecificCondition endFT = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.END_FT)
                .timestamp(LocalDateTime.now().minusMinutes(50))
                .build();
        specificConditions.add(beginFT);
        specificConditions.add(endFT);

        driverService.addSpecificCondition(specificConditions);
        driverService.removeSpecificCondition(beginFT);

        assertThat(driverService.getSpecificConditions()).isEmpty();

    }

    @Test
    public void testRemoveSpecificConditionsRemoveBeginOutOfScope() throws SpecificConditionException {
        DriverService driverService = DriverService.getInstance();
        ArrayList<SpecificCondition> specificConditions = new ArrayList<>();
        SpecificCondition beginOutOfScope = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.BEGIN_OUT_OF_SCOPE)
                .timestamp(LocalDateTime.now().minusMinutes(100))
                .build();
        SpecificCondition endOutOfScope = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.END_OUT_OF_SCOPE)
                .timestamp(LocalDateTime.now().minusMinutes(50))
                .build();
        specificConditions.add(beginOutOfScope);
        specificConditions.add(endOutOfScope);

        driverService.addSpecificCondition(specificConditions);

        specificConditions.clear();

        specificConditions.add(endOutOfScope);

        driverService.removeSpecificCondition(beginOutOfScope);

        assertThat(driverService.getSpecificConditions()).isEmpty();

    }

    @Test
    public void testRemoveSpecificConditionsRemoveEndOutOfScope() throws SpecificConditionException {
        DriverService driverService = DriverService.getInstance();
        ArrayList<SpecificCondition> specificConditions = new ArrayList<>();
        SpecificCondition beginOutOfScope = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.BEGIN_OUT_OF_SCOPE)
                .timestamp(LocalDateTime.now().minusMinutes(100))
                .build();
        SpecificCondition endOutOfScope = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.END_OUT_OF_SCOPE)
                .timestamp(LocalDateTime.now().minusMinutes(50))
                .build();
        specificConditions.add(beginOutOfScope);
        specificConditions.add(endOutOfScope);

        driverService.addSpecificCondition(specificConditions);

        specificConditions.clear();

        specificConditions.add(endOutOfScope);

        driverService.removeSpecificCondition(endOutOfScope);

        assertThat(driverService.getSpecificConditions()).isEmpty();

    }

    @Test
    public void testRemoveBeginSpecificConditionsWithSameTimestamp() throws SpecificConditionException {
        DriverService driverService = DriverService.getInstance();
        ArrayList<SpecificCondition> specificConditions = new ArrayList<>();
        SpecificCondition beginOutOfScope = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.BEGIN_OUT_OF_SCOPE)
                .timestamp(LocalDateTime.now())
                .build();
        SpecificCondition endOutOfScope = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.END_OUT_OF_SCOPE)
                .timestamp(LocalDateTime.now())
                .build();
        specificConditions.add(beginOutOfScope);
        specificConditions.add(endOutOfScope);

        driverService.addSpecificCondition(specificConditions);

        specificConditions.clear();

        specificConditions.add(endOutOfScope);

        driverService.removeSpecificCondition(endOutOfScope);

        assertThat(driverService.getSpecificConditions()).isEmpty();

    }

    @Test
    public void specificConditionRoundTripTest() throws SpecificConditionException {
        DriverService driverService = DriverService.getInstance();
        ArrayList<SpecificCondition> specificConditions = new ArrayList<>();

        // Create and add 6 specific conditions
        SpecificCondition beginFT1 = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.BEGIN_FT)
                .timestamp(LocalDateTime.now().minusMinutes(100))
                .build();
        SpecificCondition endFT1 = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.END_FT)
                .timestamp(LocalDateTime.now().minusMinutes(90))
                .build();
        SpecificCondition beginOutOfScope1 = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.BEGIN_OUT_OF_SCOPE)
                .timestamp(LocalDateTime.now().minusMinutes(80))
                .build();
        SpecificCondition endOutOfScope1 = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.END_OUT_OF_SCOPE)
                .timestamp(LocalDateTime.now().minusMinutes(70))
                .build();
        SpecificCondition beginFT2 = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.BEGIN_FT)
                .timestamp(LocalDateTime.now().minusMinutes(60))
                .build();
        SpecificCondition endFT2 = SpecificCondition.builder()
                .specificConditionType(SpecificConditionType.END_FT)
                .timestamp(LocalDateTime.now().minusMinutes(50))
                .build();

        specificConditions.add(beginFT1);
        specificConditions.add(endFT1);

        driverService.addSpecificCondition(specificConditions);
        specificConditions.clear();


        specificConditions.add(beginOutOfScope1);
        specificConditions.add(endOutOfScope1);

        driverService.addSpecificCondition(specificConditions);
        specificConditions.clear();


        specificConditions.add(beginFT2);
        specificConditions.add(endFT2);

        driverService.addSpecificCondition(specificConditions);
        specificConditions.clear();


        // Assert that all conditions are in the list
        assertThat(driverService.getSpecificConditions()).containsExactly(beginFT1, endFT1, beginOutOfScope1, endOutOfScope1, beginFT2, endFT2);

        // Remove the middle ones (beginOutOfScope1 and endOutOfScope1)
        driverService.removeSpecificCondition(beginOutOfScope1);

        // Assert that only the middle ones got removed and the rest are still there
        assertThat(driverService.getSpecificConditions()).containsExactly(beginFT1, endFT1, beginFT2, endFT2);
    }


    @AfterEach
    public void cleanUp() {
        DriverService.getInstance().clear();
    }

}
