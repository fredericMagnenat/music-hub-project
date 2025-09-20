package com.musichub.artist.application.service.exception;

/**
 * Exception thrown when database operations fail during artist enrichment.
 * This exception is used to wrap database-related errors in the ArtistEnrichmentService.
 */
public class ArtistEnrichmentDatabaseException extends RuntimeException {

    /**
     * Constructs a new ArtistEnrichmentDatabaseException with the specified detail message.
     *
     * @param message the detail message
     */
    public ArtistEnrichmentDatabaseException(String message) {
        super(message);
    }

    /**
     * Constructs a new ArtistEnrichmentDatabaseException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public ArtistEnrichmentDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new ArtistEnrichmentDatabaseException with the specified cause.
     *
     * @param cause the cause of this exception
     */
    public ArtistEnrichmentDatabaseException(Throwable cause) {
        super(cause);
    }
}