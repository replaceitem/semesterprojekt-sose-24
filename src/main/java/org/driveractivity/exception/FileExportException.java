package org.driveractivity.exception;

public class FileExportException extends AlertedException {
    public FileExportException(String message) {
        super(message);
    }

    public FileExportException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getTitle() {
        return "File Export Error";
    }
}
