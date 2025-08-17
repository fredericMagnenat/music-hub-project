package com.musichub.producer.domain.values;

import java.util.Objects;
import java.util.Set;

/**
 * Value object representing the origin of track metadata.
 */
public record Source(String platform, String apiVersion) {

    private static final Set<String> ALLOWED_PLATFORMS = Set.of(
            "SPOTIFY", "TIDAL", "APPLE_MUSIC", "YOUTUBE_MUSIC", "DEEZER", "BANDCAMP"
    );

    public Source {
        Objects.requireNonNull(platform, "platform must not be null");
        Objects.requireNonNull(apiVersion, "apiVersion must not be null");
        String normalizedPlatform = platform.trim().toUpperCase();
        if (normalizedPlatform.isEmpty()) {
            throw new IllegalArgumentException("platform must not be blank");
        }
        if (!ALLOWED_PLATFORMS.contains(normalizedPlatform)) {
            throw new IllegalArgumentException("Unsupported platform: " + platform);
        }
        String normalizedApiVersion = apiVersion.trim();
        if (normalizedApiVersion.isEmpty()) {
            throw new IllegalArgumentException("apiVersion must not be blank");
        }
        // assign normalized values to record components
        platform = normalizedPlatform;
        apiVersion = normalizedApiVersion;
    }

    public static Source of(String platform, String apiVersion) {
        return new Source(platform, apiVersion);
    }
}
