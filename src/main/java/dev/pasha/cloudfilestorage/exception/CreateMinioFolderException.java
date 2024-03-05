package dev.pasha.cloudfilestorage.exception;

public class CreateMinioFolderException extends RuntimeException {
    public CreateMinioFolderException(String message, Exception cause) {
        super(message, cause);
    }
}
