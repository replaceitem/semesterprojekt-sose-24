package org.driveractivity.exception;

public class SpecificConditionException extends AlertedException {
    public SpecificConditionException(String message) {
        super("Specific Condition Exception", message);
    }
}
