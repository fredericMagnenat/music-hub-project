package com.musichub.shared.events;

/**
 * Artist credit information for domain events.
 * Contains artist name and optionally their unique ID.
 * Based on domain charter specification for events.
 */
public record ArtistCreditInfo(
    String artistName,
    String artistId  // UUID as string, may be null for unresolved artists
) {
    
    /**
     * Creates an ArtistCreditInfo with only the artist name.
     * @param artistName the artist name
     * @return ArtistCreditInfo with null artistId
     */
    public static ArtistCreditInfo withName(String artistName) {
        return new ArtistCreditInfo(artistName, null);
    }
    
    /**
     * Creates an ArtistCreditInfo with both name and ID.
     * @param artistName the artist name
     * @param artistId the artist ID as string
     * @return ArtistCreditInfo with both fields
     */
    public static ArtistCreditInfo with(String artistName, String artistId) {
        return new ArtistCreditInfo(artistName, artistId);
    }
}