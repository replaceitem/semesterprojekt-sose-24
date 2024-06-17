package org.driveractivity.util;

import javafx.scene.paint.Color;

public class ColorUtil {
    /**
     * @param color Original color
     * @param factor How much to brighten. When 1 will return white
     * @return Brightened color
     */
    public static Color brighten(Color color, double factor) {
        return Color.hsb(color.getHue(), (1-factor) * color.getSaturation(), color.getBrightness() + factor * (1-color.getBrightness()), color.getOpacity());
    }

    /**
     * @return The color with opacity replaced
     */
    public static Color withOpacity(Color color, double opacity) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity);
    }
}
