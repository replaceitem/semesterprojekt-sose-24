package org.driveractivity.exception;

public class SpecificConditionException extends AlertedException {
    public SpecificConditionException(String message) {
        super(message);
    }

    public SpecificConditionException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getTitle() {
        return "Specific Condition Exception";
    }
}
