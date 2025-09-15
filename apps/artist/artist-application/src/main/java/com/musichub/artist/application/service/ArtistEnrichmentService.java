package com.musichub.artist.application.service;

import com.musichub.artist.application.ports.out.ArtistReconciliationPort;
import com.musichub.artist.application.ports.out.ArtistRepository;
import com.musichub.artist.domain.model.Artist;
import com.musichub.artist.domain.model.ArtistStatus;
import com.musichub.shared.domain.values.SourceType;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Application service for enriching artists with external source data.
 * Implements the Source of Truth Hierarchy defined in the domain charter.
 */
@ApplicationScoped
public class ArtistEnrichmentService {

    private final ArtistRepository artistRepository;
    private final List<ArtistReconciliationPort> reconciliationPorts;

    // Source of Truth Hierarchy as defined in domain charter
    private static final List<SourceType> SOURCE_HIERARCHY = Arrays.asList(
        SourceType.MANUAL,   // Highest priority
        SourceType.TIDAL,
        SourceType.SPOTIFY,
        SourceType.DEEZER,
        SourceType.APPLE_MUSIC  // Lowest priority
    );

    @Inject
    public ArtistEnrichmentService(ArtistRepository artistRepository,
                                   List<ArtistReconciliationPort> reconciliationPorts) {
        this.artistRepository = artistRepository;
        this.reconciliationPorts = reconciliationPorts;
    }

    /**
     * Enriches a provisional artist by searching external sources.
     * Applies Source of Truth Hierarchy when multiple sources are found.
     *
     * @param artist the provisional artist to enrich
     * @return CompletableFuture containing the enriched artist
     */
    public CompletableFuture<Artist> enrichArtist(Artist artist) {
        if (artist.getStatus() != ArtistStatus.PROVISIONAL) {
            // Already verified, no need to enrich
            return CompletableFuture.completedFuture(artist);
        }

        // Try to find artist in external sources according to hierarchy
        return searchInExternalSources(artist.getNameValue())
            .thenApply(externalArtist -> {
                if (externalArtist.isPresent()) {
                    // Merge external data with existing artist
                    Artist enriched = mergeArtistData(artist, externalArtist.get());
                    return artistRepository.save(enriched);
                }
                // No external data found, keep as provisional
                return artist;
            });
    }

    /**
     * Searches for an artist in external sources following the Source of Truth Hierarchy.
     *
     * @param artistName the artist name to search for
     * @return CompletableFuture containing the artist from the highest priority source
     */
    private CompletableFuture<Optional<Artist>> searchInExternalSources(String artistName) {
        // Try sources in hierarchy order
        CompletableFuture<Optional<Artist>> result = CompletableFuture.completedFuture(Optional.empty());

        for (SourceType sourceType : SOURCE_HIERARCHY) {
            result = result.thenCompose(currentResult -> {
                if (currentResult.isPresent()) {
                    // Already found in higher priority source
                    return CompletableFuture.completedFuture(currentResult);
                }

                // Try current source type
                return searchInSource(artistName, sourceType);
            });
        }

        return result;
    }

    /**
     * Searches for an artist in a specific source.
     *
     * @param artistName the artist name to search for
     * @param sourceType the source type to search in
     * @return CompletableFuture containing the artist if found
     */
    private CompletableFuture<Optional<Artist>> searchInSource(String artistName, SourceType sourceType) {
        return reconciliationPorts.stream()
            .filter(port -> port.supports(sourceType))
            .findFirst()
            .map(port -> port.findArtistByName(artistName, sourceType))
            .orElse(CompletableFuture.completedFuture(Optional.empty()));
    }

    /**
     * Merges external artist data with the existing provisional artist.
     * The external artist's sources and verification status are applied.
     * The name is updated based on source priority hierarchy.
     *
     * @param existingArtist the current provisional artist
     * @param externalArtist the artist data from external source
     * @return the merged artist
     */
    private Artist mergeArtistData(Artist existingArtist, Artist externalArtist) {
        Artist merged = existingArtist;

        // Add all sources from external artist
        for (var source : externalArtist.getSources()) {
            merged = merged.addSource(source);
        }

        // Update name using the external artist's name and let Artist decide based on priority
        // Since we know external artist comes from a single source type (highest found in hierarchy)
        if (!externalArtist.getSources().isEmpty()) {
            // Use the first source since external artist comes from single source search
            var sourceType = externalArtist.getSources().get(0).sourceType();
            merged = merged.updateNameFromSource(externalArtist.getName(), sourceType);
        }

        // Mark as verified if external artist is verified
        if (externalArtist.getStatus() == ArtistStatus.VERIFIED) {
            merged = merged.markAsVerified();
        }

        return merged;
    }


}