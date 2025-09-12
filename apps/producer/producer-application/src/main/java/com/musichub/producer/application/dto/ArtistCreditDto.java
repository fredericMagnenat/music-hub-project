package com.musichub.producer.application.dto;

import java.util.UUID;

public class ArtistCreditDto {

    private final String artistName;
    private final UUID artistId;

    public ArtistCreditDto(String artistName, UUID artistId) {
        this.artistName = artistName;
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public UUID getArtistId() {
        return artistId;
    }
}
