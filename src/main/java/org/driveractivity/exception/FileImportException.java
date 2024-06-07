package org.driveractivity.exception;

import lombok.Getter;

public class FileImportException extends Exception {
    @Getter
    private final String title = "File Import Error";
    public FileImportException(String message) {
        super(message);
    }

}
