package com.musichub.shared.domain.id;

/**
 * Exception thrown when ID generation fails due to algorithm unavailability or other issues.
 */
public class IdGenerationException extends RuntimeException {

    public IdGenerationException(String message) {
        super(message);
    }

    public IdGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
