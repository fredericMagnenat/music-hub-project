package com.musichub.producer.adapter.rest.util;

import org.slf4j.Logger;

/**
 * Utility class for standardized error handling in REST resources.
 */
public class ErrorHandler {

    private static final String FAILED_TO_PREFIX = "Failed to ";

    private ErrorHandler() {
        // Utility class
    }

    /**
     * Handles an exception by logging it and wrapping it in a RuntimeException.
     *
     * @param log the logger to use
     * @param correlationId the correlation ID for tracing
     * @param operation the operation that failed
     * @param e the original exception
     * @return a RuntimeException wrapping the original exception
     */
    public static RuntimeException handleException(Logger log, String correlationId,
                                                 String operation, Exception e) {
        log.error("Failed to {} (correlationId: {})", operation, correlationId, e);
        return new RuntimeException(FAILED_TO_PREFIX + operation, e);
    }

    /**
     * Handles an exception by logging it and wrapping it in a specific exception type.
     *
     * @param log the logger to use
     * @param correlationId the correlation ID for tracing
     * @param operation the operation that failed
     * @param e the original exception
     * @param exceptionType the type of exception to create
     * @param <T> the exception type
     * @return an exception of the specified type
     */
    @SuppressWarnings("unchecked")
    public static <T extends RuntimeException> T handleException(Logger log, String correlationId,
                                                               String operation, Exception e,
                                                               Class<T> exceptionType) {
        log.error("Failed to {} (correlationId: {})", operation, correlationId, e);
        try {
            return exceptionType.getConstructor(String.class, Throwable.class)
                    .newInstance(FAILED_TO_PREFIX + operation + " (correlationId: " + correlationId + ")", e);
        } catch (Exception ex) {
            // Fallback to RuntimeException if the specific exception cannot be instantiated
            return (T) new RuntimeException(FAILED_TO_PREFIX + operation + " (correlationId: " + correlationId + ")", e);
        }
    }
}
