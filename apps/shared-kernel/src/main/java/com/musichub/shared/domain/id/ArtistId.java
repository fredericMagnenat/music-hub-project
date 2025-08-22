package com.musichub.shared.domain.id;

import java.util.UUID;

public record ArtistId(UUID value) {
    
    private static final UUID NAMESPACE_ARTIST = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");

    public ArtistId { 
        if (value == null) throw new IllegalArgumentException("ArtistId null"); 
    }
    
    public static ArtistId newId() { 
        return new ArtistId(UUID.randomUUID()); 
    }
    
    public static ArtistId fromName(String artistName) {
        if (artistName == null) {
            throw new IllegalArgumentException("Artist name must not be null");
        }
        if (artistName.trim().isEmpty()) {
            throw new IllegalArgumentException("Artist name must not be empty");
        }
        UUID uuid = IdGenerator.generateUUIDv5(NAMESPACE_ARTIST, artistName.trim());
        return new ArtistId(uuid);
    }
}
