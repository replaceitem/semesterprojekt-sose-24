package org.driveractivity.exception;

public class FileImportException extends AlertedException {
    public FileImportException(String message) {
        super(message);
    }

    public FileImportException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getTitle() {
        return "File Import Error";
    }
}
