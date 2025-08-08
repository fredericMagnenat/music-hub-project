package com.musichub.artist.application.port.in;


import com.musichub.shared.events.TrackWasRegistered;

public interface ArtistTrackRegistrationUseCase {
    void handleTrackRegistration(TrackWasRegistered event);
}