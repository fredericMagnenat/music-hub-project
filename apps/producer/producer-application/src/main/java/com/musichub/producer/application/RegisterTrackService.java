package com.musichub.producer.application;

import com.musichub.producer.application.dto.ExternalTrackMetadata;
import com.musichub.producer.application.exception.ExternalServiceException;
import com.musichub.producer.application.ports.out.MusicPlatformPort;
import com.musichub.producer.domain.model.Producer;
import com.musichub.producer.domain.model.Track;
import com.musichub.producer.domain.ports.in.RegisterTrackUseCase;
import com.musichub.producer.domain.ports.out.ProducerRepository;
import com.musichub.producer.domain.values.Source;
import com.musichub.shared.domain.values.ISRC;
import com.musichub.shared.domain.values.ProducerCode;
import com.musichub.shared.events.TrackWasRegistered;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@ApplicationScoped
public class RegisterTrackService implements RegisterTrackUseCase {

    private static final Logger logger = LoggerFactory.getLogger(RegisterTrackService.class);

    private final ProducerRepository producerRepository;
    private final MusicPlatformPort musicPlatformPort;
    private final Event<TrackWasRegistered> trackRegisteredEvent;

    @Inject
    public RegisterTrackService(
            ProducerRepository producerRepository,
            MusicPlatformPort musicPlatformPort,
            Event<TrackWasRegistered> trackRegisteredEvent) {
        this.producerRepository = Objects.requireNonNull(producerRepository);
        this.musicPlatformPort = Objects.requireNonNull(musicPlatformPort);
        this.trackRegisteredEvent = Objects.requireNonNull(trackRegisteredEvent);
    }

    @Override
    @Transactional
    public Producer registerTrack(String isrcValue) {
        logger.debug("Registering track with ISRC: {}", isrcValue);
        
        // 1. NEW: Fetch track metadata from external API FIRST
        // This will throw ExternalServiceException if it fails, preventing further processing
        ExternalTrackMetadata metadata = fetchTrackMetadata(isrcValue);
        
        // 2. Existing logic: normalize ISRC, find/create Producer
        // Only proceed if external API call was successful
        ISRC isrc = ISRC.of(normalizeIsrc(isrcValue));
        ProducerCode code = ProducerCode.with(isrc);

        Producer producer = producerRepository.findByProducerCode(code)
                .orElseGet(() -> Producer.createNew(code, null));
        
        // 3. NEW: Create Track domain object
        Track track = createTrackFromMetadata(metadata);
        
        // 4. NEW: Add track to producer (idempotent)
        boolean wasAdded = producer.addTrack(track);
        
        // 5. Save producer to database
        Producer savedProducer = producerRepository.save(producer);
        
        // 6. NEW: Publish event only if track was actually added
        if (wasAdded) {
            logger.info("Track was added to producer, publishing TrackWasRegistered event for ISRC: {}", isrcValue);
            publishTrackWasRegisteredEvent(track, savedProducer);
        } else {
            logger.debug("Track already exists in producer, no event will be published for ISRC: {}", isrcValue);
        }
        
        return savedProducer;
    }

    /**
     * Fetches track metadata from the external music platform API.
     * 
     * @param isrcValue The ISRC to search for
     * @return ExternalTrackMetadata containing track information
     * @throws ExternalServiceException if the track cannot be found or API fails
     */
    private ExternalTrackMetadata fetchTrackMetadata(String isrcValue) {
        logger.debug("Fetching track metadata from external API for ISRC: {}", isrcValue);
        
        try {
            ExternalTrackMetadata metadata = musicPlatformPort.getTrackByIsrc(isrcValue);
            if (metadata == null) {
                throw new ExternalServiceException(
                    "No track metadata returned for ISRC: " + isrcValue,
                    isrcValue,
                    "external-api"
                );
            }
            logger.debug("Successfully fetched metadata for ISRC: {} - Title: '{}' by {}", 
                        isrcValue, metadata.getTitle(), metadata.getArtistNames());
            return metadata;
        } catch (ExternalServiceException e) {
            logger.error("Failed to fetch track metadata from external API for ISRC: {} - {}", 
                        isrcValue, e.getMessage());
            throw e; // Re-throw the application exception
        } catch (Exception e) {
            logger.error("Unexpected error while fetching track metadata for ISRC: {}", isrcValue, e);
            throw new ExternalServiceException(
                "Unexpected error fetching track metadata for ISRC: " + isrcValue,
                isrcValue,
                "external-api",
                e
            );
        }
    }

    /**
     * Creates a Track domain object from external API metadata.
     * 
     * @param metadata The metadata from the external API
     * @return Track domain object
     */
    private Track createTrackFromMetadata(ExternalTrackMetadata metadata) {
        logger.debug("Creating Track domain object from metadata: {}", metadata);
        
        ISRC isrc = ISRC.of(normalizeIsrc(metadata.getIsrc()));
        Source source = Source.of(metadata.getPlatform().toUpperCase(), "v2");
        
        Track track = Track.of(isrc, metadata.getTitle(), metadata.getArtistNames(), source);
        logger.debug("Created Track: {}", track);
        
        return track;
    }

    /**
     * Publishes TrackWasRegistered event to the Vert.x event bus.
     * 
     * @param track The track that was registered
     * @param producer The producer that owns the track
     */
    private void publishTrackWasRegisteredEvent(Track track, Producer producer) {
        logger.debug("Publishing TrackWasRegistered event for track: {}", track.isrc().value());
        
        TrackWasRegistered event = new TrackWasRegistered(
            track.isrc(),
            track.title(),
            track.artistNames()
        );
        
        trackRegisteredEvent.fire(event);
        
        logger.info("Successfully published TrackWasRegistered event for ISRC: {} - Title: '{}' by {}", 
                   track.isrc().value(), track.title(), track.artistNames());
    }

    private static String normalizeIsrc(String input) {
        if (input == null) {
            throw new IllegalArgumentException("ISRC value must not be null");
        }
        return input.replace("-", "").trim().toUpperCase();
    }
}
