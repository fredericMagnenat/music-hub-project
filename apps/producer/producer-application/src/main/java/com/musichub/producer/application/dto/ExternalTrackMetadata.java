package com.musichub.producer.application.dto;

import java.util.List;

/**
 * DTO representing track metadata from external music platforms.
 * This is used for communication between the application layer and external services.
 */
public class ExternalTrackMetadata {

    private final String isrc;
    private final String title;
    private final List<ArtistCreditDto> artistCredits;
    private final String platform;

    public ExternalTrackMetadata(String isrc, String title, List<ArtistCreditDto> artistCredits, String platform) {
        this.isrc = isrc;
        this.title = title;
        this.artistCredits = List.copyOf(artistCredits != null ? artistCredits : List.of());
        this.platform = platform;
    }

    public String getIsrc() {
        return isrc;
    }

    public String getTitle() {
        return title;
    }

    public List<ArtistCreditDto> getArtistCredits() {
        return artistCredits;
    }

    /**
     * Convenience method to get artist names as strings.
     * @return list of artist names
     */
    public List<String> getArtistNames() {
        return artistCredits.stream()
                .map(ArtistCreditDto::getArtistName)
                .toList();
    }

    public String getPlatform() {
        return platform;
    }

    @Override
    public String toString() {
        return "ExternalTrackMetadata{" +
                "isrc='" + isrc + "'" +
                ", title='" + title + "'" +
                ", artistCredits=" + artistCredits +
                ", platform='" + platform + "'" +
                "}";
    }
}