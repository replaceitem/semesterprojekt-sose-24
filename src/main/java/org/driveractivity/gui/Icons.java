package org.driveractivity.gui;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignA;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignD;
import org.kordamp.ikonli.materialdesign2.MaterialDesignS;

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

    public static final Ikon CARD_INSERTED = MaterialDesignC.CREDIT_CARD;
    public static final Ikon CARD_NOT_INSERTED = MaterialDesignC.CREDIT_CARD_OFF;
}