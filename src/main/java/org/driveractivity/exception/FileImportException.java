package org.driveractivity.exception;

import org.jetbrains.annotations.*;

public class FileImportException extends AlertedException {
    public FileImportException(String message) {
        super(message);
    }

    public FileImportException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public @NotNull String getTitle() {
        return "File Import Error";
    }
}
