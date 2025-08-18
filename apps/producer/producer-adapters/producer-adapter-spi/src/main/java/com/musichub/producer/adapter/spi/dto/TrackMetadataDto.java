package com.musichub.producer.adapter.spi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Data Transfer Object for track metadata received from external music platforms.
 * This DTO maps the JSON response from external APIs to Java objects.
 */
public class TrackMetadataDto {

    /**
     * International Standard Recording Code
     */
    @JsonProperty("isrc")
    public String isrc;

    /**
     * Track title
     */
    @JsonProperty("title")
    public String title;

    /**
     * List of artist names associated with the track
     */
    @JsonProperty("artists")
    public List<String> artistNames;

    /**
     * Platform identifier (e.g., "tidal", "spotify")
     */
    @JsonProperty("platform")
    public String platform;

    /**
     * Default constructor for JSON deserialization
     */
    public TrackMetadataDto() {
    }

    /**
     * Constructor for creating instances in tests or other scenarios
     */
    public TrackMetadataDto(String isrc, String title, List<String> artistNames, String platform) {
        this.isrc = isrc;
        this.title = title;
        this.artistNames = artistNames;
        this.platform = platform;
    }

    @Override
    public String toString() {
        return "TrackMetadataDto{" +
                "isrc='" + isrc + '\'' +
                ", title='" + title + '\'' +
                ", artistNames=" + artistNames +
                ", platform='" + platform + '\'' +
                '}';
    }
}