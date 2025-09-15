package com.musichub.artist.adapter.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 * Response DTO representing an artist's contribution to a track.
 * Part of the API contract for Artist REST endpoints.
 */
public class ContributionResponse {

    @JsonProperty("trackId")
    public UUID trackId;

    @JsonProperty("title")
    public String title;

    @JsonProperty("isrc")
    public String isrc;

    public ContributionResponse() {
        // Default constructor for Jackson
    }

    public ContributionResponse(UUID trackId, String title, String isrc) {
        this.trackId = trackId;
        this.title = title;
        this.isrc = isrc;
    }
}