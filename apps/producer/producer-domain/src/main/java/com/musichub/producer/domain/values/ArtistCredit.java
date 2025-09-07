package com.musichub.producer.domain.values;

import com.musichub.shared.domain.id.ArtistId;

import java.util.Objects;

/**
 * Value object representing an artist credit on a track.
 * Contains the artist's name and optionally their unique ID.
 * Based on domain charter specification.
 */
public record ArtistCredit(String artistName, ArtistId artistId) {

    public ArtistCredit {
        Objects.requireNonNull(artistName, "artistName must not be null");
        if (artistName.isBlank()) {
            throw new IllegalArgumentException("artistName must not be blank");
        }
        // artistId can be null for provisional tracks where artist is not yet resolved
    }

    /**
     * Creates an ArtistCredit with only the artist name (no ID).
     * Used when the artist hasn't been resolved to an Artist entity yet.
     * 
     * @param artistName the name of the artist
     * @return ArtistCredit with null artistId
     */
    public static ArtistCredit withName(String artistName) {
        return new ArtistCredit(artistName, null);
    }

    /**
     * Creates an ArtistCredit with both name and ID.
     * Used when the artist has been resolved to an Artist entity.
     * 
     * @param artistName the name of the artist
     * @param artistId the unique ID of the artist
     * @return ArtistCredit with both name and ID
     */
    public static ArtistCredit with(String artistName, ArtistId artistId) {
        return new ArtistCredit(artistName, artistId);
    }

    /**
     * Updates this credit with an artist ID, typically after artist resolution.
     * 
     * @param newArtistId the artist ID to associate
     * @return new ArtistCredit instance with the ID
     */
    public ArtistCredit withArtistId(ArtistId newArtistId) {
        return new ArtistCredit(this.artistName, newArtistId);
    }

    /**
     * Returns true if this credit has been resolved with an artist ID.
     * 
     * @return true if artistId is not null
     */
    public boolean isResolved() {
        return artistId != null;
    }
}
