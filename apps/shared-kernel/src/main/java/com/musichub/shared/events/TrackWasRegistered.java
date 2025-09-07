package com.musichub.shared.events;

import com.musichub.shared.domain.values.ISRC;

import java.util.List;
import java.util.UUID;

/**
 * Domain event published when a track is successfully registered in the Producer context.
 * Contains all required data as specified in the domain charter:
 * - isrc: The track's unique identifier
 * - title: The track title
 * - producerId: The ID of the producer that owns this track
 * - artistCredits: List of artist credits with names and optional IDs  
 * - sources: List of external sources where this track metadata was found
 */
public record TrackWasRegistered(
    ISRC isrc,
    String title,
    UUID producerId,
    List<ArtistCreditInfo> artistCredits,
    List<SourceInfo> sources
) {}