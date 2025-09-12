package com.musichub.producer.domain.values;

import com.musichub.shared.domain.values.Source;
import com.musichub.shared.domain.values.SourceType;

import java.util.Map;

/**
 * Producer-specific priority logic for Source of Truth Hierarchy.
 * Lower values indicate higher priority: MANUAL(1) > TIDAL(2) > SPOTIFY(3) > DEEZER(4) > APPLE_MUSIC(5)
 */
public enum ProducerSourcePriority {
    MANUAL(1),
    TIDAL(2),
    SPOTIFY(3),
    DEEZER(4),
    APPLE_MUSIC(5);

    private final int priorityValue;

    private static final Map<SourceType, ProducerSourcePriority> SOURCE_TYPE_MAPPING = Map.of(
        SourceType.MANUAL, MANUAL,
        SourceType.TIDAL, TIDAL,
        SourceType.SPOTIFY, SPOTIFY,
        SourceType.DEEZER, DEEZER,
        SourceType.APPLE_MUSIC, APPLE_MUSIC
    );

    ProducerSourcePriority(int priorityValue) {
        this.priorityValue = priorityValue;
    }

    public int getPriorityValue() {
        return priorityValue;
    }

    /**
     * Creates a ProducerSourcePriority from a SourceType.
     *
     * @param sourceType the source type
     * @return the corresponding priority
     */
    public static ProducerSourcePriority fromSourceType(SourceType sourceType) {
        return SOURCE_TYPE_MAPPING.get(sourceType);
    }

    /**
     * Creates a ProducerSourcePriority from a Source.
     *
     * @param source the source
     * @return the corresponding priority
     */
    public static ProducerSourcePriority fromSource(Source source) {
        if (source == null) {
            throw new IllegalArgumentException("Source cannot be null");
        }
        return fromSourceType(source.sourceType());
    }

    /**
     * Determines if this priority has higher precedence than another.
     * Lower priority values indicate higher precedence.
     *
     * @param other the other priority to compare
     * @return true if this has higher precedence
     */
    public boolean hasHigherPriorityThan(ProducerSourcePriority other) {
        if (other == null) {
            throw new IllegalArgumentException("Other priority cannot be null");
        }
        return this.priorityValue < other.priorityValue;
    }

    /**
     * Compares this priority with another for ordering.
     * Lower values come first (higher priority).
     *
     * @param other the other priority
     * @return negative if this has higher priority, positive if lower, 0 if equal
     */
    public int compareByPriority(ProducerSourcePriority other) {
        if (other == null) {
            throw new IllegalArgumentException("Other priority cannot be null");
        }
        return Integer.compare(this.priorityValue, other.priorityValue);
    }
}