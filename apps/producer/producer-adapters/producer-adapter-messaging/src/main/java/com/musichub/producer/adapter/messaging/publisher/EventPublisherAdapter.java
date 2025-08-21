package com.musichub.producer.adapter.messaging.publisher;

import com.musichub.producer.application.ports.out.EventPublisherPort;
import com.musichub.shared.events.TrackWasRegistered;
import io.vertx.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class EventPublisherAdapter implements EventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(EventPublisherAdapter.class);

    @Inject
    EventBus eventBus;

    @Override
    public void publishTrackRegistered(TrackWasRegistered event) {
        log.debug("Publishing TrackWasRegistered event for ISRC: {}", event.isrc().value());
        this.eventBus.publish("track-registered", event);
        log.info("Successfully published TrackWasRegistered event for ISRC: {} - Title: '{}'", 
                 event.isrc().value(), event.title());
    }
}
