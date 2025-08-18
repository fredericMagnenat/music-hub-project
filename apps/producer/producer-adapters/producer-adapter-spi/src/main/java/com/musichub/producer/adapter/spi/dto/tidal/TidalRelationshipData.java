package com.musichub.producer.adapter.spi.dto.tidal;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Relationship data structure in JSON:API format.
 * Contains references to related resources by ID and type.
 */
public class TidalRelationshipData {

    /**
     * Array of resource identifiers
     */
    @JsonProperty("data")
    public List<TidalResourceIdentifier> data;

    public TidalRelationshipData() {
    }

    public TidalRelationshipData(List<TidalResourceIdentifier> data) {
        this.data = data;
    }

    /**
     * Check if this relationship has any data
     */
    public boolean hasData() {
        return data != null && !data.isEmpty();
    }
}