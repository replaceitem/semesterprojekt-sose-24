package org.driveractivity.util;

import javafx.scene.paint.Color;

public class ColorUtil {
    /**
     * @return The color with opacity replaced
     */
    public static Color withOpacity(Color color, double opacity) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity);
    }
}
