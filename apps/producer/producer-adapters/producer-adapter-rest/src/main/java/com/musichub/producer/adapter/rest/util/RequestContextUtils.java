package com.musichub.producer.adapter.rest.util;

import java.util.UUID;

import org.slf4j.MDC;

/**
 * Utility class for managing request context, including correlation ID generation and MDC management.
 */
public class RequestContextUtils {

    private RequestContextUtils() {
        // Utility class
    }

    /**
     * Generates a new correlation ID and sets it in the MDC context.
     *
     * @return the generated correlation ID
     */
    public static String generateCorrelationId() {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        return correlationId;
    }

    /**
     * Cleans up the MDC context by removing the correlation ID.
     */
    public static void cleanup() {
        MDC.remove("correlationId");
    }
}
