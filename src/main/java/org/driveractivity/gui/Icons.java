package org.driveractivity.gui;

import javafx.scene.image.Image;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.*;

import java.io.*;

public class Icons {
    
    public static FontIcon create(Ikon icon, int size) {
        FontIcon fontIcon = new FontIcon(icon);
        fontIcon.setIconSize(size);
        return fontIcon;
    }
    
    // See: https://kordamp.org/ikonli/cheat-sheet-materialdesign2.html
    
    public static final Ikon EDIT = MaterialDesignS.SQUARE_EDIT_OUTLINE;
    public static final Ikon DELETE = MaterialDesignD.DELETE_OUTLINE;
    public static final Ikon INSERT_BEFORE = MaterialDesignA.ARROW_EXPAND_LEFT;
    public static final Ikon INSERT_AFTER = MaterialDesignA.ARROW_EXPAND_RIGHT;

    public static final Ikon CARD_NOT_INSERTED = MaterialDesignC.CREDIT_CARD_OFF;
    
    public static final Ikon FERRY_TRAIN = MaterialDesignF.FERRY;
    public static final Ikon OUT_OF_SCOPE = MaterialDesignB.BORDER_NONE_VARIANT;
    
    
    public static final Image APP_ICON_16 = loadResourceImage("icons/icon16.png");
    public static final Image APP_ICON_32 = loadResourceImage("icons/icon32.png");
    public static final Image APP_ICON_64 = loadResourceImage("icons/icon64.png");
    public static final Image APP_ICON_128 = loadResourceImage("icons/icon128.png");
    
    public static final Image[] APP_ICONS = {APP_ICON_16, APP_ICON_32, APP_ICON_64, APP_ICON_128};
    
    private static Image loadResourceImage(String name) {
        try(InputStream inputStream = Icons.class.getResourceAsStream(name)) {
            if(inputStream == null) throw new IOException("Resource not found");
            return new Image(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Error loading " + name, e);
        }
    }
}
