package dev.pasha.cloudfilestorage.exception;

public class PutMinioObjectException extends RuntimeException {
    public PutMinioObjectException(String message, Throwable cause) {
        super(message, cause);
    }
}
