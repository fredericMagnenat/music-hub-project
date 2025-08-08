package com.musichub.artist.adapter.messaging;

import com.musichub.artist.application.port.in.ArtistTrackRegistrationUseCase;
import com.musichub.shared.events.TrackWasRegistered;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class TrackEventHandler {

    private static final Logger log = LoggerFactory.getLogger(TrackEventHandler.class);

    @Inject
    ArtistTrackRegistrationUseCase artistService;

    @ConsumeEvent("track-registered")
    @Blocking
    public void handle(TrackWasRegistered event) {
        log.info("Received track registration event: {}", event);
        artistService.handleTrackRegistration(event);
    }
}