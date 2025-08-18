package com.musichub.producer.adapter.spi.exception;

/**
 * Exception thrown when a track cannot be found in external music platform services
 * or when external service communication fails.
 * 
 * This exception is used to handle scenarios such as:
 * - Track not found (404) in the external API
 * - External service temporarily unavailable (5xx errors)
 * - Network timeouts or connectivity issues
 * - Invalid API responses
 */
public class TrackNotFoundInExternalServiceException extends RuntimeException {

    private final String isrc;
    private final String platform;

    /**
     * Creates a new exception for track not found scenarios
     * 
     * @param message descriptive error message
     * @param isrc the ISRC that was searched for
     * @param platform the platform that was queried (e.g., "tidal")
     */
    public TrackNotFoundInExternalServiceException(String message, String isrc, String platform) {
        super(message);
        this.isrc = isrc;
        this.platform = platform;
    }

    /**
     * Creates a new exception for track not found scenarios with cause
     * 
     * @param message descriptive error message
     * @param isrc the ISRC that was searched for
     * @param platform the platform that was queried
     * @param cause the underlying cause of the exception
     */
    public TrackNotFoundInExternalServiceException(String message, String isrc, String platform, Throwable cause) {
        super(message, cause);
        this.isrc = isrc;
        this.platform = platform;
    }

    /**
     * @return the ISRC that could not be found
     */
    public String getIsrc() {
        return isrc;
    }

    /**
     * @return the platform that was queried
     */
    public String getPlatform() {
        return platform;
    }
}