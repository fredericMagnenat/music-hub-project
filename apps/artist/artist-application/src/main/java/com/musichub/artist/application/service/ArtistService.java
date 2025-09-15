package com.musichub.artist.application.service;

import com.musichub.artist.application.ports.in.ArtistTrackRegistrationUseCase;
import com.musichub.artist.application.ports.out.ArtistRepository;
import com.musichub.artist.domain.model.Artist;
import com.musichub.artist.domain.model.ArtistStatus;
import com.musichub.shared.domain.id.TrackId;
import com.musichub.artist.domain.values.Contribution;
import com.musichub.shared.events.TrackWasRegistered;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;

/**
 * Application service handling track registration events for the Artist context.
 * Creates or updates artist aggregates based on track registration events.
 * Automatically enriches provisional artists with external source data.
 */
@ApplicationScoped
public class ArtistService implements ArtistTrackRegistrationUseCase {

    private final ArtistRepository artistRepository;
    private final ArtistEnrichmentService enrichmentService;

    @Inject
    public ArtistService(ArtistRepository artistRepository,
                        ArtistEnrichmentService enrichmentService) {
        this.artistRepository = artistRepository;
        this.enrichmentService = enrichmentService;
    }

    @Override
    public void handleTrackRegistration(TrackWasRegistered event) {
        if (event.artistCredits() == null || event.artistCredits().isEmpty()) {
            return; // No artists to process
        }

        // Create contribution from event data
        Contribution contribution = Contribution.of(
            new TrackId(event.producerId()), // Using producerId as trackId for now
            event.title(),
            event.isrc()
        );

        // Process each artist credit
        for (var artistCredit : event.artistCredits()) {
            processArtistCredit(artistCredit.artistName(), contribution);
        }
    }

    /**
     * Processes a single artist credit: finds or creates artist, adds contribution, enriches if provisional.
     *
     * @param artistName the name of the artist
     * @param contribution the contribution to add
     */
    private void processArtistCredit(String artistName, Contribution contribution) {
        Optional<Artist> existingArtist = artistRepository.findByName(artistName);

        Artist artist;
        if (existingArtist.isPresent()) {
            artist = existingArtist.get();
        } else {
            // Create new provisional artist
            artist = Artist.createProvisional(artistName);
            artist = artistRepository.save(artist);

            // Trigger enrichment asynchronously for provisional artists
            if (artist.getStatus() == ArtistStatus.PROVISIONAL) {
                enrichmentService.enrichArtist(artist)
                    .thenAccept(enrichedArtist -> {
                        // Artist is already saved by the enrichment service
                    })
                    .exceptionally(throwable -> {
                        // Log error but don't fail the main flow
                        return null;
                    });
            }
        }

        // Add the contribution to the artist
        Artist updatedArtist = artist.addContribution(contribution);
        artistRepository.save(updatedArtist);
    }
}