package com.musichub.producer.adapter.spi.dto.tidal;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Track attributes from Tidal API response.
 * Contains the actual track data like title, ISRC, duration, etc.
 */
public class TidalTrackAttributes {

    /**
     * Track title
     */
    @JsonProperty("title")
    public String title;

    /**
     * International Standard Recording Code (ISRC)
     */
    @JsonProperty("isrc")
    public String isrc;

    /**
     * Track duration in ISO 8601 format (e.g., "PT3M45S")
     */
    @JsonProperty("duration")
    public String duration;

    /**
     * Whether the track contains explicit content
     */
    @JsonProperty("explicit")
    public Boolean explicit;

    /**
     * Track popularity score (0.0 - 1.0)
     */
    @JsonProperty("popularity")
    public Double popularity;

    /**
     * Copyright information
     */
    @JsonProperty("copyright")
    public String copyright;

    /**
     * Creation timestamp (ISO 8601)
     */
    @JsonProperty("createdAt")
    public String createdAt;

    /**
     * Genre tags associated with the track
     */
    @JsonProperty("genreTags")
    public List<String> genreTags;

    /**
     * Media format tags (e.g., "HIRES_LOSSLESS")
     */
    @JsonProperty("mediaTags")
    public List<String> mediaTags;

    /**
     * Musical key
     */
    @JsonProperty("key")
    public String key;

    /**
     * Scale of the musical key
     */
    @JsonProperty("keyScale")
    public String keyScale;

    /**
     * Beats per minute
     */
    @JsonProperty("bpm")
    public Double bpm;

    public TidalTrackAttributes() {
    }

    public TidalTrackAttributes(String title, String isrc) {
        this.title = title;
        this.isrc = isrc;
    }
}