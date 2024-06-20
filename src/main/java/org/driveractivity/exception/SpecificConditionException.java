package org.driveractivity.exception;

public class SpecificConditionException extends AlertedException {
    public SpecificConditionException(String title, String message) {
        super(title, message);
    }
}
