package com.musichub.shared.domain.id;

import java.util.UUID;

public record TrackId(UUID value) {
    
    private static final UUID NAMESPACE_TRACK = UUID.fromString("550e8400-e29b-41d4-a716-446655440003");

    public TrackId { 
        if (value == null) throw new IllegalArgumentException("TrackId null"); 
    }
    
    public static TrackId newId() { 
        return new TrackId(UUID.randomUUID()); 
    }
    
    public static TrackId fromISRC(String isrc) {
        if (isrc == null) {
            throw new IllegalArgumentException("ISRC must not be null");
        }
        if (isrc.trim().isEmpty()) {
            throw new IllegalArgumentException("ISRC must not be empty");
        }
        UUID uuid = IdGenerator.generateUUIDv5(NAMESPACE_TRACK, isrc.trim());
        return new TrackId(uuid);
    }
}