package com.musichub.producer.domain.values;

import java.util.Objects;

/**
 * Enum representing the priority hierarchy for source of truth determination.
 * Based on domain charter: MANUAL (Highest) > TIDAL > SPOTIFY > DEEZER > APPLE_MUSIC (Lowest)
 */
public enum SourcePriority {
    
    MANUAL(1),
    TIDAL(2),
    SPOTIFY(3),
    DEEZER(4),
    APPLE_MUSIC(5);
    
    private final int priority;
    
    SourcePriority(int priority) {
        this.priority = priority;
    }
    
    public int getPriority() {
        return priority;
    }
    
    /**
     * Returns the SourcePriority for a given source name.
     * 
     * @param sourceName the source name (case-insensitive)
     * @return the corresponding SourcePriority
     * @throws IllegalArgumentException if the source name is not supported
     */
    public static SourcePriority fromSourceName(String sourceName) {
        Objects.requireNonNull(sourceName, "sourceName must not be null");
        String normalized = sourceName.trim().toUpperCase();
        
        return switch (normalized) {
            case "MANUAL" -> MANUAL;
            case "TIDAL" -> TIDAL;
            case "SPOTIFY" -> SPOTIFY;
            case "DEEZER" -> DEEZER;
            case "APPLE_MUSIC" -> APPLE_MUSIC;
            default -> throw new IllegalArgumentException("Unsupported source name: " + sourceName);
        };
    }
    
    /**
     * Determines if this source has higher priority than another source.
     * Lower numerical priority values indicate higher business priority.
     * 
     * @param other the other source priority to compare
     * @return true if this source has higher priority (lower numerical value)
     */
    public boolean hasHigherPriorityThan(SourcePriority other) {
        Objects.requireNonNull(other, "other must not be null");
        return this.priority < other.priority;
    }
}
