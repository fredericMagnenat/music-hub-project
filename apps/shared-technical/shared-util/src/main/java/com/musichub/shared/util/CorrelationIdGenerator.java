package com.musichub.shared.util;

import java.util.UUID;

/**
 * Utility class for generating and managing correlation IDs across services.
 * Provides consistent correlation ID generation and formatting.
 */
public final class CorrelationIdGenerator {

    private CorrelationIdGenerator() {
        // Utility class
    }

    /**
     * Builds a service-specific correlation ID from an incoming correlation ID.
     * If incoming ID is provided, appends service suffix.
     * If incoming ID is null/empty, generates a new one.
     *
     * @param incomingCorrelationId The correlation ID from the caller (can be null)
     * @param serviceName The name of the service (e.g., "producer", "artist")
     * @return Service-specific correlation ID
     */
    public static String buildServiceCorrelationId(String incomingCorrelationId, String serviceName) {
        if (incomingCorrelationId != null && !incomingCorrelationId.trim().isEmpty()) {
            return incomingCorrelationId + "-" + serviceName + "-service";
        }
        return generateServiceCorrelationId(serviceName);
    }

    /**
     * Generates a service-specific correlation ID.
     * Format: {serviceName}-{timestamp}-{uuid-safe}
     * Uses full UUID without hyphens to avoid collisions and ensure uniqueness.
     *
     * @param serviceName The name of the service
     * @return Generated service-specific correlation ID
     */
    public static String generateServiceCorrelationId(String serviceName) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return serviceName + "-" + timestamp + "-" + uuid;
    }

    /**
     * Extracts the original correlation ID from a service-specific one.
     * Useful for logging and debugging.
     *
     * @param serviceCorrelationId The service-specific correlation ID
     * @param serviceName The name of the service
     * @return Original correlation ID or the service ID if not found
     */
    public static String extractOriginalCorrelationId(String serviceCorrelationId, String serviceName) {
        if (serviceCorrelationId == null || serviceCorrelationId.isEmpty()) {
            return serviceCorrelationId;
        }

        String suffix = "-" + serviceName + "-service";
        if (serviceCorrelationId.endsWith(suffix)) {
            return serviceCorrelationId.substring(0, serviceCorrelationId.length() - suffix.length());
        }

        return serviceCorrelationId;
    }
}