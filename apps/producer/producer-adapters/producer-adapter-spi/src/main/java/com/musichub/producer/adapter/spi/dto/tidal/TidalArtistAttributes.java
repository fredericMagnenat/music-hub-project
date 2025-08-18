package com.musichub.producer.adapter.spi.dto.tidal;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Artist attributes from Tidal API response.
 * Contains artist-specific data like name, popularity, etc.
 */
public class TidalArtistAttributes {

    /**
     * Artist name
     */
    @JsonProperty("name")
    public String name;

    /**
     * Artist popularity score (0.0 - 1.0)
     */
    @JsonProperty("popularity")
    public Double popularity;

    /**
     * Creation timestamp (ISO 8601)
     */
    @JsonProperty("createdAt")
    public String createdAt;

    /**
     * Whether the artist is active
     */
    @JsonProperty("active")
    public Boolean active;

    /**
     * Artist roles/credits for this track
     */
    @JsonProperty("roles")
    public List<String> roles;

    public TidalArtistAttributes() {
    }

    public TidalArtistAttributes(String name) {
        this.name = name;
    }

    public TidalArtistAttributes(String name, Double popularity, List<String> roles) {
        this.name = name;
        this.popularity = popularity;
        this.roles = roles;
    }
}