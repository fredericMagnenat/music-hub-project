package com.musichub.producer.adapter.spi.dto.tidal;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a track resource in Tidal's JSON:API response structure.
 * This follows the JSON:API specification for resource objects.
 */
public class TidalTrackData {

    /**
     * Unique identifier for the track
     */
    @JsonProperty("id")
    public String id;

    /**
     * Resource type (always "tracks" for track resources)
     */
    @JsonProperty("type")
    public String type;

    /**
     * Track attributes (title, ISRC, etc.)
     */
    @JsonProperty("attributes")
    public TidalTrackAttributes attributes;

    /**
     * Relationships to other resources (artists, albums, etc.)
     */
    @JsonProperty("relationships")
    public TidalTrackRelationships relationships;

    public TidalTrackData() {
    }

    public TidalTrackData(String id, String type, TidalTrackAttributes attributes, TidalTrackRelationships relationships) {
        this.id = id;
        this.type = type;
        this.attributes = attributes;
        this.relationships = relationships;
    }
}