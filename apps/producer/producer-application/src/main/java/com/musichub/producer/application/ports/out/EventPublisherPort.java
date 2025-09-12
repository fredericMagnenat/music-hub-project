package com.musichub.producer.application.ports.out;

import com.musichub.shared.events.TrackWasRegistered;

/**
 * Port for publishing domain events from the Producer context.
 * 
 * <p>This outbound port follows the hexagonal architecture pattern,
 * allowing the application layer to publish events without knowing
 * the specific implementation details of the event publishing mechanism.
 * 
 * <p>The implementation is provided by the adapter layer and could be
 * backed by various messaging systems (Vert.x EventBus, Apache Kafka, etc.)
 */
public interface EventPublisherPort {
    
    /**
     * Publishes a TrackWasRegistered event.
     * 
     * @param event The track registration event to publish
     */
    void publishTrackRegistered(TrackWasRegistered event);
}