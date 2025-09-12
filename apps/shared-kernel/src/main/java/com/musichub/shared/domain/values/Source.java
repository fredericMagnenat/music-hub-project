package com.musichub.shared.domain.values;

import java.util.Objects;

/**
 * Value object representing the origin of track metadata.
 * Shared across bounded contexts for consistency.
 */
public record Source(SourceType sourceType, String sourceId) {

    public Source {
        Objects.requireNonNull(sourceType, "sourceType must not be null");
        Objects.requireNonNull(sourceId, "sourceId must not be null");

        String normalizedSourceId = sourceId.trim();
        if (normalizedSourceId.isEmpty()) {
            throw new IllegalArgumentException("sourceId must not be blank");
        }
        // assign normalized value to record component
        sourceId = normalizedSourceId;
    }

    /**
     * Creates a Source from string values.
     * The sourceName will be converted to SourceType.
     *
     * @param sourceName the source name as string
     * @param sourceId the source identifier
     * @return a new Source instance
     * @throws IllegalArgumentException if sourceName is invalid
     */
    public static Source of(String sourceName, String sourceId) {
        Objects.requireNonNull(sourceName, "sourceName must not be null");
        SourceType sourceType = SourceType.fromString(sourceName);
        return new Source(sourceType, sourceId);
    }

    /**
     * Gets the source name as string.
     *
     * @return the source name
     */
    public String getSourceName() {
        return sourceType.getValue();
    }
}