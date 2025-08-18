package com.musichub.producer.application.dto;

import java.util.List;

/**
 * DTO representing track metadata from external music platforms.
 * This is used for communication between the application layer and external services.
 */
public class ExternalTrackMetadata {

    private final String isrc;
    private final String title;
    private final List<String> artistNames;
    private final String platform;

    public ExternalTrackMetadata(String isrc, String title, List<String> artistNames, String platform) {
        this.isrc = isrc;
        this.title = title;
        this.artistNames = List.copyOf(artistNames != null ? artistNames : List.of());
        this.platform = platform;
    }

    public String getIsrc() {
        return isrc;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getArtistNames() {
        return artistNames;
    }

    public String getPlatform() {
        return platform;
    }

    @Override
    public String toString() {
        return "ExternalTrackMetadata{" +
                "isrc='" + isrc + '\'' +
                ", title='" + title + '\'' +
                ", artistNames=" + artistNames +
                ", platform='" + platform + '\'' +
                '}';
    }
}