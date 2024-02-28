package dev.pasha.cloudfilestorage.exception;

public class DeleteMinioObjectException extends RuntimeException {
    public DeleteMinioObjectException(String message, Throwable cause) {
        super(message, cause);
    }
}
