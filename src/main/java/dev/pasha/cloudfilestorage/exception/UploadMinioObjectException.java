package dev.pasha.cloudfilestorage.exception;

public class UploadMinioObjectException extends RuntimeException {
    public UploadMinioObjectException(String message, Throwable cause) {
        super(message, cause);
    }
}
