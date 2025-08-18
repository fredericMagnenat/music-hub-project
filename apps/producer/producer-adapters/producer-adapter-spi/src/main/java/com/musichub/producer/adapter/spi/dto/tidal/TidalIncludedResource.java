package com.musichub.producer.adapter.spi.dto.tidal;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Included resource in Tidal's JSON:API response.
 * Represents full resource data for related objects (artists, albums, etc.)
 * that were requested via the 'include' parameter.
 */
public class TidalIncludedResource {

    /**
     * Unique identifier of the resource
     */
    @JsonProperty("id")
    public String id;

    /**
     * Type of the resource (e.g., "artists", "albums")
     */
    @JsonProperty("type")
    public String type;

    /**
     * Resource attributes - structure depends on resource type
     */
    @JsonProperty("attributes")
    public TidalArtistAttributes attributes;

    public TidalIncludedResource() {
    }

    public TidalIncludedResource(String id, String type, TidalArtistAttributes attributes) {
        this.id = id;
        this.type = type;
        this.attributes = attributes;
    }

    /**
     * Check if this resource is an artist
     */
    public boolean isArtist() {
        return "artists".equals(type);
    }

    /**
     * Check if this resource is an album
     */
    public boolean isAlbum() {
        return "albums".equals(type);
    }
}