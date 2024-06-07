package org.driveractivity.exception;

import lombok.Getter;

@Getter
public class AlertedException extends Exception {
    private final String title;

    public AlertedException(String title, String message) {
        super(message);
        this.title = title;
    }
}
