package org.driveractivity.exception;

import org.jetbrains.annotations.*;

public abstract class AlertedException extends Exception {
    public AlertedException(String message) {
        super(message);
    }

    public AlertedException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public abstract @NotNull String getTitle();
}
