package com.musichub.artist.application.ports.in;


import com.musichub.shared.events.TrackWasRegistered;

public interface ArtistTrackRegistrationUseCase {
    void handleTrackRegistration(TrackWasRegistered event);
}