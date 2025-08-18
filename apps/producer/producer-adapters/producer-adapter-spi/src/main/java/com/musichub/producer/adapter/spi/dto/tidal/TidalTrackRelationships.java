package com.musichub.producer.adapter.spi.dto.tidal;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Track relationships in Tidal's JSON:API structure.
 * Contains references to related resources like artists and albums.
 */
public class TidalTrackRelationships {

    /**
     * Related artists for this track
     */
    @JsonProperty("artists")
    public TidalRelationshipData artists;

    /**
     * Related albums for this track
     */
    @JsonProperty("albums")
    public TidalRelationshipData albums;

    /**
     * Related lyrics for this track
     */
    @JsonProperty("lyrics")
    public TidalRelationshipData lyrics;

    /**
     * Track owners/rights holders
     */
    @JsonProperty("owners")
    public TidalRelationshipData owners;

    /**
     * Content providers
     */
    @JsonProperty("providers")
    public TidalRelationshipData providers;

    /**
     * Similar tracks
     */
    @JsonProperty("similarTracks")
    public TidalRelationshipData similarTracks;

    /**
     * Track statistics
     */
    @JsonProperty("trackStatistics")
    public TidalRelationshipData trackStatistics;

    public TidalTrackRelationships() {
    }
}