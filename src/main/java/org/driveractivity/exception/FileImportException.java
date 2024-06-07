package org.driveractivity.exception;

public class FileImportException extends AlertedException {
    public FileImportException(String message) {
        super("File Import Error", message);
    }
}
