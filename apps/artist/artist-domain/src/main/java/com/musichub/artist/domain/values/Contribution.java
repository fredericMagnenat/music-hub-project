package com.musichub.artist.domain.values;

import com.musichub.shared.domain.id.TrackId;
import com.musichub.shared.domain.values.ISRC;

import java.util.Objects;

/**
 * Value object representing an artist's contribution to a track.
 * Immutable and used across bounded contexts for consistency.
 * Based on domain charter specification.
 */
public record Contribution(
    TrackId trackId,
    String title,
    ISRC isrc
) {

    public Contribution {
        Objects.requireNonNull(trackId, "trackId cannot be null");
        Objects.requireNonNull(title, "title cannot be null");
        Objects.requireNonNull(isrc, "isrc cannot be null");

        String normalizedTitle = title.trim();
        if (normalizedTitle.isEmpty()) {
            throw new IllegalArgumentException("title cannot be blank");
        }
        // Assign normalized value
        title = normalizedTitle;
    }

    /**
     * Creates a Contribution from basic parameters.
     *
     * @param trackId the track identifier
     * @param title the track title
     * @param isrc the track ISRC
     * @return a new Contribution instance
     */
    public static Contribution of(TrackId trackId, String title, ISRC isrc) {
        return new Contribution(trackId, title, isrc);
    }
}