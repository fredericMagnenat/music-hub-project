package com.musichub.producer.domain.exception;

/**
 * Exception thrown when there is an error registering a track.
 */
public class TrackRegistrationException extends RuntimeException {

    public TrackRegistrationException(String message) {
        super(message);
    }

    public TrackRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
