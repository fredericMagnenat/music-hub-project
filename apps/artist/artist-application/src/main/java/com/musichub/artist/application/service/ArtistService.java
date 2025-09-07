package com.musichub.artist.application.service;

import com.musichub.artist.domain.model.Artist;
import com.musichub.artist.application.ports.in.ArtistTrackRegistrationUseCase;
import com.musichub.artist.application.ports.out.ArtistRepository;
import com.musichub.shared.events.TrackWasRegistered;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ArtistService implements ArtistTrackRegistrationUseCase {

    private final ArtistRepository artistRepository;

    @Inject
    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Override
    public void handleTrackRegistration(TrackWasRegistered event) {
        if (event.artistCredits() == null || event.artistCredits().isEmpty()) {
            return; // Or log a warning
        }

        for (var artistCredit : event.artistCredits()) {
            Artist artist = artistRepository.findByName(artistCredit.artistName())
                    .orElseGet(() -> Artist.createProvisional(artistCredit.artistName()));

            artist.addTrackReference(event.isrc());
            artistRepository.save(artist);
        }
    }
}