package org.driveractivity.exception;

public abstract class AlertedException extends Exception {
    public AlertedException(String message) {
        super(message);
    }

    public AlertedException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public abstract String getTitle();
}
