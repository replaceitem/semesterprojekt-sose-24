package org.driveractivity.util;

import org.junit.jupiter.api.*;

import java.time.*;

import static java.time.LocalDateTime.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.driveractivity.util.TimeUtil.isBetween;

public class TimeUtilTest {
    @Test
    public void testIsBetween() {
        LocalDateTime start = of(2024, Month.JUNE, 29, 22, 55);
        LocalDateTime end = of(2024, Month.JULY, 1, 12, 0);
        
        assertThat(isBetween(of(2024, Month.JUNE, 28, 12, 0), start, end)).isFalse();
        assertThat(isBetween(start, start, end)).isTrue();
        assertThat(isBetween(of(2024, Month.JUNE, 30, 2, 0), start, end)).isTrue();
        assertThat(isBetween(end, start, end)).isFalse();
        assertThat(isBetween(of(2024, Month.JULY, 1, 12, 1), start, end)).isFalse();
    }
}
