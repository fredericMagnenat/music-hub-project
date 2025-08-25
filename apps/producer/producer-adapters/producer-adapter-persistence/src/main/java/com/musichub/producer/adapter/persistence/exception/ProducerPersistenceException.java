package com.musichub.producer.adapter.persistence.exception;

/**
 * Exception thrown when persistence operations on Producer aggregates fail.
 * This is a dedicated exception for the persistence adapter layer.
 */
public class ProducerPersistenceException extends RuntimeException {

    public ProducerPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProducerPersistenceException(String message) {
        super(message);
    }
}
