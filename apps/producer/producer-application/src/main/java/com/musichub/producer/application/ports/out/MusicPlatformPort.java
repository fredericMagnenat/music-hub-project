package com.musichub.producer.application.ports.out;

import com.musichub.producer.application.dto.ExternalTrackMetadata;

/**
 * Port (interface) for accessing external music platform APIs.
 * This follows the hexagonal architecture pattern where the application layer
 * defines the interface and the adapter layer provides the implementation.
 */
public interface MusicPlatformPort {

    /**
     * Retrieves track metadata from external music platform by ISRC.
     * 
     * @param isrc The International Standard Recording Code
     * @return ExternalTrackMetadata containing track information
     */
    ExternalTrackMetadata getTrackByIsrc(String isrc);
}