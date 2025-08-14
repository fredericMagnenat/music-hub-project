package com.musichub.artist.domain.model;

import com.musichub.shared.domain.id.ArtistId;
import com.musichub.shared.domain.values.ISRC;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Artist {

    private final ArtistId id;
    private final String name;
    private ArtistStatus status;
    private final Set<ISRC> trackReferences;

    private Artist(ArtistId id, String name, ArtistStatus status, Set<ISRC> trackReferences) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.trackReferences = new HashSet<>(trackReferences);
    }

    public static Artist createProvisional(String name) {
        Objects.requireNonNull(name, "Artist name cannot be null");
        return new Artist(new ArtistId(UUID.randomUUID()), name, ArtistStatus.PROVISIONAL, new HashSet<>());
    }

    public static Artist from(ArtistId id, String name, ArtistStatus status, Set<ISRC> trackReferences) {
        return new Artist(id, name, status, trackReferences);
    }

    public void addTrackReference(ISRC isrc) {
        Objects.requireNonNull(isrc, "ISRC cannot be null");
        this.trackReferences.add(isrc);
    }

    public ArtistId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArtistStatus getStatus() {
        return status;
    }

    public Set<ISRC> getTrackReferences() {
        return new HashSet<>(trackReferences);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artist artist = (Artist) o;
        return id.equals(artist.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
