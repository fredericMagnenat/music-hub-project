package com.musichub.shared.domain.id;

import java.util.UUID;

public record ArtistId(UUID value) {
    public ArtistId { if (value == null) throw new IllegalArgumentException("ArtistId null"); }
    public static ArtistId newId() { return new ArtistId(UUID.randomUUID()); }
}
