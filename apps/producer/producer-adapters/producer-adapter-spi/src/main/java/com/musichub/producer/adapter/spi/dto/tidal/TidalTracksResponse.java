package com.musichub.producer.adapter.spi.dto.tidal;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Response wrapper for Tidal tracks API following JSON:API specification.
 * This represents the top-level response structure from /tracks endpoint.
 */
public class TidalTracksResponse {

    /**
     * Array of track data objects
     */
    @JsonProperty("data")
    public List<TidalTrackData> data;

    /**
     * Included resources (artists, albums, etc.) when using include parameter
     */
    @JsonProperty("included")
    public List<TidalIncludedResource> included;

    /**
     * Metadata about the response
     */
    @JsonProperty("meta")
    public TidalMeta meta;

    /**
     * Links for pagination
     */
    @JsonProperty("links")
    public TidalLinks links;

    public TidalTracksResponse() {
    }

    /**
     * Check if the response contains any tracks
     */
    public boolean hasData() {
        return data != null && !data.isEmpty();
    }

    /**
     * Get the first track from the response (useful when searching by ISRC)
     */
    public TidalTrackData getFirstTrack() {
        if (hasData()) {
            return data.get(0);
        }
        return null;
    }
}