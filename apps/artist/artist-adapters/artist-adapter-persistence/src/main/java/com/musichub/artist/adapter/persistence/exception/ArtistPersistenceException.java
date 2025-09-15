package com.musichub.artist.adapter.persistence.exception;

/**
 * Exception thrown when persistence operations on Artist aggregates fail.
 * This is a dedicated exception for the persistence adapter layer.
 */
public class ArtistPersistenceException extends RuntimeException {

    public ArtistPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArtistPersistenceException(String message) {
        super(message);
    }
}
