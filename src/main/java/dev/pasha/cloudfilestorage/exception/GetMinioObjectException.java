package dev.pasha.cloudfilestorage.exception;

public class GetMinioObjectException extends RuntimeException {
    public GetMinioObjectException(String message, Throwable cause) {
        super(message, cause);
    }
}
