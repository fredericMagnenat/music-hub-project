package com.musichub.artist.domain.values;

import java.util.Objects;

/**
 * Value object representing an artist name with validation rules.
 * Immutable and ensures consistent artist name format across contexts.
 */
public record ArtistName(String value) {

    public ArtistName {
        Objects.requireNonNull(value, "Artist name cannot be null");

        String normalizedValue = value.trim();
        if (normalizedValue.isEmpty()) {
            throw new IllegalArgumentException("Artist name cannot be blank");
        }

        if (normalizedValue.length() > 255) {
            throw new IllegalArgumentException("Artist name cannot exceed 255 characters");
        }

        // Assign normalized value
        value = normalizedValue;
    }

    /**
     * Creates an ArtistName from a string value.
     *
     * @param name the artist name
     * @return a new ArtistName instance
     * @throws IllegalArgumentException if name is invalid
     */
    public static ArtistName of(String name) {
        return new ArtistName(name);
    }

    /**
     * Returns the artist name value as string.
     *
     * @return the artist name
     */
    @Override
    public String toString() {
        return value;
    }
}