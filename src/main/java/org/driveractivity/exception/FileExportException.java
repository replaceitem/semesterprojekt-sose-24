package org.driveractivity.exception;

import org.jetbrains.annotations.*;

public class FileExportException extends AlertedException {
    public FileExportException(String message) {
        super(message);
    }

    public FileExportException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public @NotNull String getTitle() {
        return "File Export Error";
    }
}
