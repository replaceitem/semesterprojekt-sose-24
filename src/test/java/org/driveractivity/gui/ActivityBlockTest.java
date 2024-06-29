package org.driveractivity.gui;

import org.driveractivity.entity.*;
import org.junit.jupiter.api.*;

import java.time.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.driveractivity.gui.ActivityBlock.formatDuration;

public class ActivityBlockTest {
    @Test
    public void testFormatTypeName() {
        assertThat(ActivityBlock.formatTypeName(ActivityType.DRIVING)).isEqualTo("Driving");
        assertThat(ActivityBlock.formatTypeName(ActivityType.WORK)).isEqualTo("Work");
        assertThat(ActivityBlock.formatTypeName(ActivityType.REST)).isEqualTo("Rest");
        assertThat(ActivityBlock.formatTypeName(ActivityType.AVAILABLE)).isEqualTo("Available");
    }
    
    
    @Test
    public void testFormatDuration() {
        assertThat(formatDuration(Duration.ZERO)).isEqualTo("0m");
        assertThat(formatDuration(Duration.ofMinutes(2))).isEqualTo("2m");
        assertThat(formatDuration(Duration.ofMinutes(59))).isEqualTo("59m");
        assertThat(formatDuration(Duration.ofHours(1))).isEqualTo("1h");
        assertThat(formatDuration(Duration.ofHours(5))).isEqualTo("5h");
        assertThat(formatDuration(Duration.ofHours(100))).isEqualTo("100h");
        assertThat(formatDuration(Duration.ofHours(5).plusMinutes(15))).isEqualTo("5,2h");
        assertThat(formatDuration(Duration.ofHours(5).plusMinutes(16))).isEqualTo("5,3h");
        assertThat(formatDuration(Duration.ofHours(5).plusMinutes(30))).isEqualTo("5,5h");
        assertThat(formatDuration(Duration.ofHours(5).plusMinutes(54))).isEqualTo("5,9h");
        assertThat(formatDuration(Duration.ofHours(5).plusMinutes(59))).isEqualTo("6h");
    }
}
