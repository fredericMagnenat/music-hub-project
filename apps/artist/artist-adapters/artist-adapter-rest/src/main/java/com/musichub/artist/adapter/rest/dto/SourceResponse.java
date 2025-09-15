package com.musichub.artist.adapter.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response DTO representing an artist's external source.
 * Part of the API contract for Artist REST endpoints.
 */
public class SourceResponse {

    @JsonProperty("sourceType")
    public String sourceType;

    @JsonProperty("sourceId")
    public String sourceId;

    public SourceResponse() {
        // Default constructor for Jackson
    }

    public SourceResponse(String sourceType, String sourceId) {
        this.sourceType = sourceType;
        this.sourceId = sourceId;
    }
}