package com.musichub.producer.adapter.spi.dto.tidal;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Resource identifier in JSON:API format.
 * Used to reference related resources by their ID and type.
 */
public class TidalResourceIdentifier {

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

    public TidalResourceIdentifier() {
    }

    public TidalResourceIdentifier(String id, String type) {
        this.id = id;
        this.type = type;
    }
}