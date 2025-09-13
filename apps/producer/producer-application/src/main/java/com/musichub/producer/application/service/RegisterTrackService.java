package com.musichub.producer.application.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

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
import com.musichub.shared.util.CorrelationIdGenerator;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class RegisterTrackService implements RegisterTrackUseCase {

        private static final Logger logger = LoggerFactory.getLogger(RegisterTrackService.class);

        // Constants for correlation ID generation
        private static final String SERVICE_NAME = "producer";

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
        public Producer registerTrack(String isrcValue, String correlationId) {
                // Generate service-specific correlation ID
                String serviceCorrelationId = CorrelationIdGenerator.buildServiceCorrelationId(correlationId,
                                SERVICE_NAME);

                Instant startTime = Instant.now();

                // Set MDC context for structured logging
                MDC.put("correlation_id", serviceCorrelationId);
                MDC.put("operation", "track_registration");
                MDC.put("service", "producer");

                try {
                        logger.info("Starting track registration for ISRC: {} (correlationId: {})", isrcValue,
                                        serviceCorrelationId);

                        // 1. NEW: Fetch track metadata from external API FIRST
                        // This will throw ExternalServiceException if it fails, preventing further
                        // processing
                        Instant apiCallStart = Instant.now();
                        ExternalTrackMetadata metadata = fetchTrackMetadata(isrcValue, serviceCorrelationId);
                        Duration apiCallDuration = Duration.between(apiCallStart, Instant.now());
                        logger.info("External API call completed in {}ms for ISRC: {} (correlationId: {})",
                                        apiCallDuration.toMillis(), isrcValue, serviceCorrelationId);

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
                                                        dto.getArtistId() != null ? new ArtistId(dto.getArtistId())
                                                                        : null))
                                        .toList();

                        boolean wasAdded = producer.registerTrack(normalizedIsrc, metadata.getTitle(), artistCredits,
                                        List.of(source));

                        // 5. Save producer to database
                        Producer savedProducer = producerRepository.save(producer);

                        logger.info("Track details saved to producer aggregate for ISRC: {} (correlationId: {})",
                                        normalizedIsrc.value(), serviceCorrelationId);

                        // 7. NEW: Publish event only if track was actually added
                        if (wasAdded) {
                                // Set business context for structured logging
                                MDC.put("business_context", Map.of(
                                                "producer_code", code.value(),
                                                "isrc", normalizedIsrc.value(),
                                                "operation", "track_registration").toString());

                                logger.info("Track was added to producer, publishing TrackWasRegistered event for ISRC: {} (correlationId: {})",
                                                isrcValue, serviceCorrelationId);
                                // Get the registered track from producer for event publishing
                                Track registeredTrack = savedProducer.getTrack(normalizedIsrc)
                                                .orElseThrow(() -> new IllegalStateException(
                                                                "Track should exist after registration"));
                                publishTrackWasRegisteredEvent(registeredTrack, savedProducer, serviceCorrelationId);
                        } else {
                                logger.debug("Track already exists in producer, no event will be published for ISRC: {} (correlationId: {})",
                                                isrcValue, serviceCorrelationId);
                        }

                        // Log total execution time
                        Duration totalDuration = Duration.between(startTime, Instant.now());
                        logger.info("Track registration completed in {}ms for ISRC: {} (correlationId: {})",
                                        totalDuration.toMillis(), isrcValue, serviceCorrelationId);

                        return savedProducer;
                } finally {
                        // Clean up MDC
                        MDC.clear();
                }
        }

        /**
         * Fetches track metadata from the external music platform API.
         *
         * @param isrcValue     The ISRC to search for
         * @param correlationId The correlation ID for tracing
         * @return ExternalTrackMetadata containing track information
         * @throws ExternalServiceException if the track cannot be found or API fails
         */
        private ExternalTrackMetadata fetchTrackMetadata(String isrcValue, String serviceCorrelationId) {
                logger.debug("Fetching track metadata from external API for ISRC: {} (correlationId: {})", isrcValue,
                                serviceCorrelationId);

                try {
                        Instant apiStart = Instant.now();
                        ExternalTrackMetadata metadata = musicPlatformPort.getTrackByIsrc(isrcValue);
                        Duration apiDuration = Duration.between(apiStart, Instant.now());

                        logger.info("External API call to musicPlatformPort.getTrackByIsrc completed in {}ms for ISRC: {} (correlationId: {})",
                                        apiDuration.toMillis(), isrcValue, serviceCorrelationId);
                        if (metadata == null) {
                                throw new ExternalServiceException(
                                                "No track metadata returned for ISRC: " + isrcValue,
                                                isrcValue,
                                                "external-api");
                        }
                        logger.info("Successfully fetched metadata for ISRC: {} - Title: '{}' by {} (correlationId: {})",
                                        isrcValue, metadata.getTitle(), metadata.getArtistCredits(),
                                        serviceCorrelationId);
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
         * @param track         The track that was registered
         * @param producer      The producer that owns the track
         * @param correlationId The correlation ID for tracing
         */
        private void publishTrackWasRegisteredEvent(Track track, Producer producer, String serviceCorrelationId) {
                logger.info("Publishing TrackWasRegistered event for track: {} (correlationId: {})",
                                track.isrc().value(), serviceCorrelationId);

                // Set business context for structured logging
                MDC.put("business_context", Map.of(
                                "producer_code", producer.producerCode().value(),
                                "isrc", track.isrc().value(),
                                "operation", "event_publishing").toString());

                // Convert domain sources to event source info
                List<SourceInfo> sources = track.sources().stream()
                                .map(source -> new SourceInfo(source.getSourceName(), source.sourceId()))
                                .toList();

                // Convert domain artist credits to event artist credit info
                List<ArtistCreditInfo> artistCredits = track.credits().stream()
                                .map(credit -> new ArtistCreditInfo(
                                                credit.artistName(),
                                                credit.artistId() != null ? credit.artistId().value().toString()
                                                                : null))
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
                                        "Successfully published TrackWasRegistered event for ISRC: {} - Title: '{}' by {} - ProducerId: {} - Sources: {} (correlationId: {})",
                                        track.isrc().value(), track.title(),
                                        artistCredits.stream().map(ArtistCreditInfo::artistName).toList(),
                                        producer.id().value(), sources.size() + " sources", serviceCorrelationId);
                }

                // Clear business context after logging
                MDC.remove("business_context");
        }

        private static String normalizeIsrc(String input) {
                if (input == null) {
                        throw new IllegalArgumentException("ISRC value must not be null");
                }
                return input.replace("-", "").trim().toUpperCase();
        }
}
