package com.musichub.producer.domain.values;

import java.util.Objects;
import java.util.Set;

/**
 * Value object representing the origin of track metadata.
 * Implements Source of Truth Hierarchy as defined in domain charter.
 */
public record Source(String sourceName, String sourceId) {

    private static final Set<String> ALLOWED_SOURCE_NAMES = Set.of(
            "SPOTIFY", "TIDAL", "DEEZER", "APPLE_MUSIC", "MANUAL"
    );

    public Source {
        Objects.requireNonNull(sourceName, "sourceName must not be null");
        Objects.requireNonNull(sourceId, "sourceId must not be null");
        String normalizedSourceName = sourceName.trim().toUpperCase();
        if (normalizedSourceName.isEmpty()) {
            throw new IllegalArgumentException("sourceName must not be blank");
        }
        if (!ALLOWED_SOURCE_NAMES.contains(normalizedSourceName)) {
            throw new IllegalArgumentException("Unsupported sourceName: " + sourceName);
        }
        String normalizedSourceId = sourceId.trim();
        if (normalizedSourceId.isEmpty()) {
            throw new IllegalArgumentException("sourceId must not be blank");
        }
        // assign normalized values to record components
        sourceName = normalizedSourceName;
        sourceId = normalizedSourceId;
    }

    public static Source of(String sourceName, String sourceId) {
        return new Source(sourceName, sourceId);
    }
    
    /**
     * Gets the priority of this source according to the Source of Truth Hierarchy.
     * Lower values indicate higher priority: MANUAL(1) > TIDAL(2) > SPOTIFY(3) > DEEZER(4) > APPLE_MUSIC(5)
     * 
     * @return the SourcePriority for this source
     */
    public SourcePriority priority() {
        return SourcePriority.fromSourceName(sourceName);
    }
    
    /**
     * Determines if this source has higher priority than another source
     * according to the Source of Truth Hierarchy.
     * 
     * @param other the other source to compare
     * @return true if this source has higher priority
     */
    public boolean hasHigherPriorityThan(Source other) {
        Objects.requireNonNull(other, "other source must not be null");
        return this.priority().hasHigherPriorityThan(other.priority());
    }
}
