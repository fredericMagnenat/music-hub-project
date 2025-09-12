package com.musichub.shared.domain.values;

import java.util.Arrays;

/**
 * Enum representing the supported source types for track metadata.
 * Used across all bounded contexts to ensure consistency.
 */
public enum SourceType {
    SPOTIFY("SPOTIFY"),
    TIDAL("TIDAL"),
    DEEZER("DEEZER"),
    APPLE_MUSIC("APPLE_MUSIC"),
    MANUAL("MANUAL");

    private final String value;

    SourceType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Creates a SourceType from a string value.
     * Case-insensitive conversion with validation.
     *
     * @param value the string representation
     * @return the corresponding SourceType
     * @throws IllegalArgumentException if the value is not supported
     */
    public static SourceType fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("SourceType value cannot be null or empty");
        }

        String normalizedValue = value.trim().toUpperCase();
        return Arrays.stream(values())
            .filter(type -> type.value.equals(normalizedValue))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unsupported source type: " + value));
    }

    @Override
    public String toString() {
        return value;
    }
}