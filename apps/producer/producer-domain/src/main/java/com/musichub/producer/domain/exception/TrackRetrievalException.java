package com.musichub.producer.domain.exception;

/**
 * Exception thrown when there is an error retrieving recent tracks.
 */
public class TrackRetrievalException extends RuntimeException {

    public TrackRetrievalException(String message) {
        super(message);
    }

    public TrackRetrievalException(String message, Throwable cause) {
        super(message, cause);
    }
}
