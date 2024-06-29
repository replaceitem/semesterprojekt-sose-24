package org.driveractivity.util;

import org.junit.jupiter.api.*;

import static javafx.scene.paint.Color.color;
import static org.assertj.core.api.Assertions.assertThat;
import static org.driveractivity.util.ColorUtil.withOpacity;

public class ColorUtilTest {
    @Test
    public void testWithOpacity() {
        assertThat(withOpacity(color(0,0,0,0), 1)).isEqualTo(color(0,0,0,1));
        assertThat(withOpacity(color(0.7, 0.8, 0.9, 1.0), 0.23)).isEqualTo(color(0.7, 0.8, 0.9, 0.23));
    }
}
