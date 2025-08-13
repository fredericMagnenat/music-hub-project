package com.musichub.artist.domain.ports.in;


import com.musichub.shared.events.TrackWasRegistered;

public interface ArtistTrackRegistrationUseCase {
    void handleTrackRegistration(TrackWasRegistered event);
}