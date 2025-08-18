package com.musichub.producer.application.exception;

/**
 * Exception thrown when external service calls fail.
 * This exception is thrown by the application layer when external dependencies
 * (like music platform APIs) are unavailable or return errors.
 */
public class ExternalServiceException extends RuntimeException {

    private final String isrc;
    private final String service;

    public ExternalServiceException(String message, String isrc, String service) {
        super(message);
        this.isrc = isrc;
        this.service = service;
    }

    public ExternalServiceException(String message, String isrc, String service, Throwable cause) {
        super(message, cause);
        this.isrc = isrc;
        this.service = service;
    }

    public String getIsrc() {
        return isrc;
    }

    public String getService() {
        return service;
    }
}