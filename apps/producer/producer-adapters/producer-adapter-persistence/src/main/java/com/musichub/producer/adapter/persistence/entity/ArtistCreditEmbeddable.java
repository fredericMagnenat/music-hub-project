package com.musichub.producer.adapter.persistence.entity;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embeddable class for artist credit persistence.
 * Maps domain ArtistCredit value object to database representation.
 * Used in @ElementCollection for track artist credits.
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistCreditEmbeddable {

    /**
     * The artist's name - required field.
     */
    @Column(name = "artist_name", nullable = false, length = 255)
    private String artistName;

    /**
     * The artist's unique ID - optional field.
     * May be null for provisional tracks where artist is not yet resolved.
     */
    @Column(name = "artist_id", nullable = true)
    private UUID artistId;

    /**
     * Creates an ArtistCreditEmbeddable with only the artist name.
     * 
     * @param artistName the artist name
     * @return ArtistCreditEmbeddable with null artistId
     */
    public static ArtistCreditEmbeddable withName(String artistName) {
        return new ArtistCreditEmbeddable(artistName, null);
    }

    /**
     * Creates an ArtistCreditEmbeddable with both name and ID.
     * 
     * @param artistName the artist name
     * @param artistId the artist ID
     * @return ArtistCreditEmbeddable with both fields
     */
    public static ArtistCreditEmbeddable with(String artistName, UUID artistId) {
        return new ArtistCreditEmbeddable(artistName, artistId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArtistCreditEmbeddable that = (ArtistCreditEmbeddable) o;
        return Objects.equals(artistName, that.artistName) &&
               Objects.equals(artistId, that.artistId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(artistName, artistId);
    }
}
