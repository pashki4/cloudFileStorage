package dev.pasha.cloudfilestorage.exception;

public class RenameMinioObjectException extends RuntimeException {
    public RenameMinioObjectException(String message, Throwable cause) {
        super(message, cause);
    }
}
