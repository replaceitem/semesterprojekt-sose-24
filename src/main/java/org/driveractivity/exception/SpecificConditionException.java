package org.driveractivity.exception;

import org.jetbrains.annotations.*;

public class SpecificConditionException extends AlertedException {
    public SpecificConditionException(String message) {
        super(message);
    }

    public SpecificConditionException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public @NotNull String getTitle() {
        return "Specific Condition Exception";
    }
}
