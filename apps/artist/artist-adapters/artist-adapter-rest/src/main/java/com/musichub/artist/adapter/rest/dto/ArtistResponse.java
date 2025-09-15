package com.musichub.artist.adapter.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

/**
 * Response DTO representing an Artist with its rich domain data.
 * Includes on-the-fly assembly of producerIds as required by AC 4.
 * Part of the API contract for Artist REST endpoints.
 */
public class ArtistResponse {

    @JsonProperty("id")
    public UUID id;

    @JsonProperty("name")
    public String name;

    @JsonProperty("status")
    public String status;

    @JsonProperty("contributions")
    public List<ContributionResponse> contributions;

    @JsonProperty("sources")
    public List<SourceResponse> sources;

    @JsonProperty("producerIds")
    public List<UUID> producerIds;

    public ArtistResponse() {
        // Default constructor for Jackson
    }

    public ArtistResponse(UUID id, String name, String status,
                         List<ContributionResponse> contributions,
                         List<SourceResponse> sources,
                         List<UUID> producerIds) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.contributions = contributions;
        this.sources = sources;
        this.producerIds = producerIds;
    }
}