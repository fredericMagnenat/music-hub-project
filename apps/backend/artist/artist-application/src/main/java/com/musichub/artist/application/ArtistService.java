package com.musichub.artist.application;

import com.musichub.artist.application.port.in.ArtistTrackRegistrationUseCase;
import com.musichub.artist.application.port.out.ArtistRepository;
import com.musichub.artist.domain.Artist;
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
        if (event.artistNames() == null || event.artistNames().isEmpty()) {
            return; // Or log a warning
        }

        for (String artistName : event.artistNames()) {
            Artist artist = artistRepository.findByName(artistName)
                    .orElseGet(() -> Artist.createProvisional(artistName));

            artist.addTrackReference(event.isrc());
            artistRepository.save(artist);
        }
    }
}