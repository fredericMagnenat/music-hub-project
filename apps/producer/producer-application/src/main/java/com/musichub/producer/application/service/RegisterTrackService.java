package com.musichub.producer.application.service;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.musichub.producer.application.dto.ExternalTrackMetadata;
import com.musichub.producer.application.exception.ExternalServiceException;
import com.musichub.producer.application.ports.in.RegisterTrackUseCase;
import com.musichub.producer.application.ports.out.EventPublisherPort;
import com.musichub.producer.application.ports.out.MusicPlatformPort;
import com.musichub.producer.application.ports.out.ProducerRepository;
import com.musichub.producer.domain.model.Producer;
import com.musichub.producer.domain.model.Track;
import com.musichub.producer.domain.values.ArtistCredit;
import com.musichub.shared.domain.id.ArtistId;
import com.musichub.shared.domain.values.ISRC;
import com.musichub.shared.domain.values.ProducerCode;
import com.musichub.shared.domain.values.Source;
import com.musichub.shared.events.ArtistCreditInfo;
import com.musichub.shared.events.SourceInfo;
import com.musichub.shared.events.TrackWasRegistered;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class RegisterTrackService implements RegisterTrackUseCase {

    private static final Logger logger = LoggerFactory.getLogger(RegisterTrackService.class);

    private final ProducerRepository producerRepository;
    private final MusicPlatformPort musicPlatformPort;
    private final EventPublisherPort eventPublisherPort;

    @Inject
    public RegisterTrackService(
            ProducerRepository producerRepository,
            MusicPlatformPort musicPlatformPort,
            EventPublisherPort eventPublisherPort) {
        this.producerRepository = Objects.requireNonNull(producerRepository);
        this.musicPlatformPort = Objects.requireNonNull(musicPlatformPort);
        this.eventPublisherPort = Objects.requireNonNull(eventPublisherPort);
    }

    @Override
    @Transactional
    public Producer registerTrack(String isrcValue) {
        logger.debug("Registering track with ISRC: {}", isrcValue);

        // 1. NEW: Fetch track metadata from external API FIRST
        // This will throw ExternalServiceException if it fails, preventing further
        // processing
        ExternalTrackMetadata metadata = fetchTrackMetadata(isrcValue);

        // 2. Existing logic: normalize ISRC, find/create Producer
        // Only proceed if external API call was successful
        ISRC normalizedIsrc = ISRC.of(normalizeIsrc(isrcValue));
        ProducerCode code = ProducerCode.with(normalizedIsrc);

        Producer producer = producerRepository.findByProducerCode(code)
                .orElseGet(() -> Producer.createNew(code, null));

        // 3. NEW: Register track with complete metadata in Producer aggregate (DDD best
        // practice)
        Source source = Source.of(metadata.getPlatform().toUpperCase(), metadata.getIsrc());
        List<ArtistCredit> artistCredits = metadata.getArtistCredits().stream()
                .map(dto -> ArtistCredit.with(dto.getArtistName(),
                        dto.getArtistId() != null ? new ArtistId(dto.getArtistId()) : null))
                .toList();

        boolean wasAdded = producer.registerTrack(normalizedIsrc, metadata.getTitle(), artistCredits, List.of(source));

        // 5. Save producer to database
        Producer savedProducer = producerRepository.save(producer);

        logger.debug("Track details saved to producer aggregate for ISRC: {}", normalizedIsrc.value());

        // 7. NEW: Publish event only if track was actually added
        if (wasAdded) {
            logger.info("Track was added to producer, publishing TrackWasRegistered event for ISRC: {}", isrcValue);
            // Get the registered track from producer for event publishing
            Track registeredTrack = savedProducer.getTrack(normalizedIsrc)
                    .orElseThrow(() -> new IllegalStateException("Track should exist after registration"));
            publishTrackWasRegisteredEvent(registeredTrack, savedProducer);
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
                        "external-api");
            }
            logger.debug("Successfully fetched metadata for ISRC: {} - Title: '{}' by {}",
                    isrcValue, metadata.getTitle(), metadata.getArtistCredits());
            return metadata;
        } catch (ExternalServiceException e) {
            throw e; // Re-throw the application exception
        } catch (Exception e) {
            throw new ExternalServiceException(
                    "Unexpected error fetching track metadata for ISRC: " + isrcValue,
                    isrcValue,
                    "external-api",
                    e);
        }
    }

    /**
     * Publishes TrackWasRegistered event to the Vert.x event bus.
     * Event contains all required data as specified in domain charter:
     * isrc, title, producerId, artistCredits, sources.
     *
     * @param track    The track that was registered
     * @param producer The producer that owns the track
     */
    private void publishTrackWasRegisteredEvent(Track track, Producer producer) {
        logger.debug("Publishing TrackWasRegistered event for track: {}", track.isrc().value());

        // Convert domain sources to event source info
        List<SourceInfo> sources = track.sources().stream()
                .map(source -> new SourceInfo(source.getSourceName(), source.sourceId()))
                .toList();

        // Convert domain artist credits to event artist credit info
        List<ArtistCreditInfo> artistCredits = track.credits().stream()
                .map(credit -> new ArtistCreditInfo(
                        credit.artistName(),
                        credit.artistId() != null ? credit.artistId().value().toString() : null))
                .toList();

        TrackWasRegistered event = new TrackWasRegistered(
                track.isrc(),
                track.title(),
                producer.id().value(),
                artistCredits,
                sources);

        this.eventPublisherPort.publishTrackRegistered(event);

        if (logger.isInfoEnabled()) {
            logger.info(
                    "Successfully published TrackWasRegistered event for ISRC: {} - Title: '{}' by {} - ProducerId: {} - Sources: {}",
                    track.isrc().value(), track.title(),
                    artistCredits.stream().map(ArtistCreditInfo::artistName).toList(),
                    producer.id().value(), sources.size() + " sources");
        }
    }

    private static String normalizeIsrc(String input) {
        if (input == null) {
            throw new IllegalArgumentException("ISRC value must not be null");
        }
        return input.replace("-", "").trim().toUpperCase();
    }
}
