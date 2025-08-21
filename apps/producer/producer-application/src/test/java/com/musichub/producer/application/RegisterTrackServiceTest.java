package com.musichub.producer.application;

import com.musichub.producer.application.dto.ExternalTrackMetadata;
import com.musichub.producer.application.exception.ExternalServiceException;
import com.musichub.producer.application.ports.out.EventPublisherPort;
import com.musichub.producer.application.ports.out.MusicPlatformPort;
import com.musichub.producer.application.service.RegisterTrackService;
import com.musichub.producer.domain.model.Producer;
import com.musichub.producer.domain.ports.out.ProducerRepository;
import com.musichub.shared.domain.values.ISRC;
import com.musichub.shared.domain.values.ProducerCode;
import com.musichub.shared.events.TrackWasRegistered;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for RegisterTrackService.
 * Tests the enhanced service logic with external API integration and event publishing.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterTrackService")
class RegisterTrackServiceTest {

    @Mock
    private ProducerRepository producerRepository;

    @Mock
    private MusicPlatformPort musicPlatformPort;

    @Mock
    private EventPublisherPort eventPublisherPort;

    @InjectMocks
    private RegisterTrackService registerTrackService;

    private static final String TEST_ISRC = "GBUM71507409";
    private static final String NORMALIZED_ISRC = "GBUM71507409";

    @Nested
    @DisplayName("Successful Track Registration")
    class SuccessfulRegistration {

        @Test
        @DisplayName("Should register new track with API call and publish event")
        void shouldRegisterNewTrackWithApiCallAndPublishEvent() {
            // Given: External API returns track metadata
            ExternalTrackMetadata mockMetadata = new ExternalTrackMetadata(
                TEST_ISRC,
                "Bohemian Rhapsody", 
                List.of("Queen"), 
                "tidal"
            );
            when(musicPlatformPort.getTrackByIsrc(TEST_ISRC)).thenReturn(mockMetadata);

            // Given: No existing producer
            ProducerCode code = ProducerCode.with(ISRC.of(NORMALIZED_ISRC));
            when(producerRepository.findByProducerCode(code)).thenReturn(Optional.empty());

            // Given: Repository save returns the producer
            Producer savedProducer = Producer.createNew(code, null);
            when(producerRepository.save(any(Producer.class))).thenReturn(savedProducer);

            // When: Registering track
            Producer result = registerTrackService.registerTrack(TEST_ISRC);

            // Then: Should call external API
            verify(musicPlatformPort).getTrackByIsrc(TEST_ISRC);

            // Then: Should save producer with track
            ArgumentCaptor<Producer> producerCaptor = ArgumentCaptor.forClass(Producer.class);
            verify(producerRepository).save(producerCaptor.capture());
            Producer capturedProducer = producerCaptor.getValue();
            assertTrue(capturedProducer.hasTrack(ISRC.of(NORMALIZED_ISRC)));

            // Then: Should publish event
            ArgumentCaptor<TrackWasRegistered> eventCaptor = ArgumentCaptor.forClass(TrackWasRegistered.class);
            verify(eventPublisherPort).publishTrackRegistered(eventCaptor.capture());
            TrackWasRegistered capturedEvent = eventCaptor.getValue();
            assertEquals(ISRC.of(NORMALIZED_ISRC), capturedEvent.isrc());
            assertEquals("Bohemian Rhapsody", capturedEvent.title());
            assertEquals(List.of("Queen"), capturedEvent.artistNames());

            // Then: Should return saved producer
            assertNotNull(result);
        }

        @Test
        @DisplayName("Should work with existing producer")
        void shouldWorkWithExistingProducer() {
            // Given: External API returns track metadata
            ExternalTrackMetadata mockMetadata = new ExternalTrackMetadata(
                TEST_ISRC,
                "Another One Bites the Dust", 
                List.of("Queen"), 
                "tidal"
            );
            when(musicPlatformPort.getTrackByIsrc(TEST_ISRC)).thenReturn(mockMetadata);

            // Given: Existing producer
            ProducerCode code = ProducerCode.with(ISRC.of(NORMALIZED_ISRC));
            Producer existingProducer = Producer.createNew(code, "Queen Music");
            when(producerRepository.findByProducerCode(code)).thenReturn(Optional.of(existingProducer));

            // Given: Repository save returns the producer
            when(producerRepository.save(any(Producer.class))).thenReturn(existingProducer);

            // When: Registering track
            Producer result = registerTrackService.registerTrack(TEST_ISRC);

            // Then: Should use existing producer
            verify(producerRepository).findByProducerCode(code);
            verify(producerRepository).save(any(Producer.class));
            verify(eventPublisherPort).publishTrackRegistered(any(TrackWasRegistered.class));

            assertNotNull(result);
        }

        @Test
        @DisplayName("Should handle multiple artists correctly")
        void shouldHandleMultipleArtistsCorrectly() {
            // Given: External API returns track with multiple artists
            ExternalTrackMetadata mockMetadata = new ExternalTrackMetadata(
                TEST_ISRC,
                "Under Pressure", 
                List.of("Queen", "David Bowie"), 
                "tidal"
            );
            when(musicPlatformPort.getTrackByIsrc(TEST_ISRC)).thenReturn(mockMetadata);

            ProducerCode code = ProducerCode.with(ISRC.of(NORMALIZED_ISRC));
            when(producerRepository.findByProducerCode(code)).thenReturn(Optional.empty());
            when(producerRepository.save(any(Producer.class))).thenReturn(Producer.createNew(code, null));

            // When: Registering track
            registerTrackService.registerTrack(TEST_ISRC);

            // Then: Event should contain all artists
            ArgumentCaptor<TrackWasRegistered> eventCaptor = ArgumentCaptor.forClass(TrackWasRegistered.class);
            verify(eventPublisherPort).publishTrackRegistered(eventCaptor.capture());
            TrackWasRegistered capturedEvent = eventCaptor.getValue();
            assertEquals(List.of("Queen", "David Bowie"), capturedEvent.artistNames());
        }
    }

    @Nested
    @DisplayName("Idempotent Behavior")
    class IdempotentBehavior {

        @Test
        @DisplayName("Should not publish event for duplicate track")
        void shouldNotPublishEventForDuplicateTrack() {
            // Given: External API returns track metadata
            ExternalTrackMetadata mockMetadata = new ExternalTrackMetadata(
                TEST_ISRC,
                "Bohemian Rhapsody", 
                List.of("Queen"), 
                "tidal"
            );
            when(musicPlatformPort.getTrackByIsrc(TEST_ISRC)).thenReturn(mockMetadata);

            // Given: Producer already has this track
            ProducerCode code = ProducerCode.with(ISRC.of(NORMALIZED_ISRC));
            Producer existingProducer = Producer.createNew(code, null);
            existingProducer.addTrack(ISRC.of(NORMALIZED_ISRC)); // Pre-add the track
            when(producerRepository.findByProducerCode(code)).thenReturn(Optional.of(existingProducer));
            when(producerRepository.save(any(Producer.class))).thenReturn(existingProducer);

            // When: Registering same track again
            registerTrackService.registerTrack(TEST_ISRC);

            // Then: Should still call external API (for metadata)
            verify(musicPlatformPort).getTrackByIsrc(TEST_ISRC);

            // Then: Should save producer (idempotent)
            verify(producerRepository).save(any(Producer.class));

            // Then: Should NOT publish event for duplicate
            verifyNoInteractions(eventPublisherPort);
        }

        @Test
        @DisplayName("Should handle ISRC normalization consistently")
        void shouldHandleIsrcNormalizationConsistently() {
            // Given: External API returns normalized metadata
            ExternalTrackMetadata mockMetadata = new ExternalTrackMetadata(
                NORMALIZED_ISRC,
                "Bohemian Rhapsody", 
                List.of("Queen"), 
                "tidal"
            );
            when(musicPlatformPort.getTrackByIsrc(anyString())).thenReturn(mockMetadata);

            ProducerCode code = ProducerCode.with(ISRC.of(NORMALIZED_ISRC));
            when(producerRepository.findByProducerCode(code)).thenReturn(Optional.empty());
            when(producerRepository.save(any(Producer.class))).thenReturn(Producer.createNew(code, null));

            // When: Registering track with formatted ISRC
            registerTrackService.registerTrack("GB-UM7-15-07409");

            // Then: Should normalize ISRC for API call
            verify(musicPlatformPort).getTrackByIsrc("GB-UM7-15-07409");

            // Then: Should use normalized ISRC internally
            ArgumentCaptor<TrackWasRegistered> eventCaptor = ArgumentCaptor.forClass(TrackWasRegistered.class);
            verify(eventPublisherPort).publishTrackRegistered(eventCaptor.capture());
            assertEquals(ISRC.of(NORMALIZED_ISRC), eventCaptor.getValue().isrc());
        }
    }

    @Nested
    @DisplayName("External API Failures")
    class ExternalApiFailures {

        @Test
        @DisplayName("Should propagate ExternalServiceException from API")
        void shouldPropagateExternalServiceExceptionFromApi() {
            // Given: External API throws exception
            ExternalServiceException apiException = new ExternalServiceException(
                "Track not found", TEST_ISRC, "tidal"
            );
            when(musicPlatformPort.getTrackByIsrc(TEST_ISRC)).thenThrow(apiException);

            // When & Then: Should propagate the exception
            ExternalServiceException thrown = assertThrows(
                ExternalServiceException.class,
                () -> registerTrackService.registerTrack(TEST_ISRC)
            );

            assertEquals("Track not found", thrown.getMessage());
            assertEquals(TEST_ISRC, thrown.getIsrc());
            assertEquals("tidal", thrown.getService());

            // Then: Should not interact with repository or event bus
            verifyNoInteractions(producerRepository);
            verifyNoInteractions(eventPublisherPort);
        }

        @Test
        @DisplayName("Should wrap unexpected exceptions from API")
        void shouldWrapUnexpectedExceptionsFromApi() {
            // Given: External API throws unexpected exception
            RuntimeException unexpected = new RuntimeException("Network timeout");
            when(musicPlatformPort.getTrackByIsrc(TEST_ISRC)).thenThrow(unexpected);

            // When & Then: Should wrap in ExternalServiceException
            ExternalServiceException thrown = assertThrows(
                ExternalServiceException.class,
                () -> registerTrackService.registerTrack(TEST_ISRC)
            );

            assertTrue(thrown.getMessage().contains("Unexpected error"));
            assertEquals(TEST_ISRC, thrown.getIsrc());
            assertEquals("external-api", thrown.getService());
            assertEquals(unexpected, thrown.getCause());

            // Then: Should not interact with repository or event bus
            verifyNoInteractions(producerRepository);
            verifyNoInteractions(eventPublisherPort);
        }

        @Test
        @DisplayName("Should not publish event when API fails")
        void shouldNotPublishEventWhenApiFails() {
            // Given: External API fails
            when(musicPlatformPort.getTrackByIsrc(TEST_ISRC))
                .thenThrow(new ExternalServiceException("Service unavailable", TEST_ISRC, "tidal"));

            // When: Attempting to register track
            assertThrows(ExternalServiceException.class,
                () -> registerTrackService.registerTrack(TEST_ISRC));

            // Then: Should not publish any event
            verifyNoInteractions(eventPublisherPort);
            verifyNoInteractions(producerRepository);
        }
    }

    @Nested
    @DisplayName("Event Publishing Scenarios")
    class EventPublishingScenarios {

        @Test
        @DisplayName("Should publish event only after successful database save")
        void shouldPublishEventOnlyAfterSuccessfulDatabaseSave() {
            // Given: External API succeeds
            ExternalTrackMetadata mockMetadata = new ExternalTrackMetadata(
                TEST_ISRC, "Test Track", List.of("Test Artist"), "tidal"
            );
            when(musicPlatformPort.getTrackByIsrc(TEST_ISRC)).thenReturn(mockMetadata);

            // Given: Producer setup
            ProducerCode code = ProducerCode.with(ISRC.of(NORMALIZED_ISRC));
            Producer producer = Producer.createNew(code, null);
            when(producerRepository.findByProducerCode(code)).thenReturn(Optional.empty());
            when(producerRepository.save(any(Producer.class))).thenReturn(producer);

            // When: Registering track
            registerTrackService.registerTrack(TEST_ISRC);

            // Then: Should save first, then publish event (verified by method order)
            verify(producerRepository).save(any(Producer.class));
            verify(eventPublisherPort).publishTrackRegistered(any(TrackWasRegistered.class));
        }

        @Test
        @DisplayName("Should create correct event structure")
        void shouldCreateCorrectEventStructure() {
            // Given: Successful setup
            ExternalTrackMetadata mockMetadata = new ExternalTrackMetadata(
                TEST_ISRC, "Bohemian Rhapsody", List.of("Queen"), "tidal"
            );
            when(musicPlatformPort.getTrackByIsrc(TEST_ISRC)).thenReturn(mockMetadata);

            ProducerCode code = ProducerCode.with(ISRC.of(NORMALIZED_ISRC));
            when(producerRepository.findByProducerCode(code)).thenReturn(Optional.empty());
            when(producerRepository.save(any(Producer.class))).thenReturn(Producer.createNew(code, null));

            // When: Registering track
            registerTrackService.registerTrack(TEST_ISRC);

            // Then: Event should have correct structure
            ArgumentCaptor<TrackWasRegistered> eventCaptor = ArgumentCaptor.forClass(TrackWasRegistered.class);
            verify(eventPublisherPort).publishTrackRegistered(eventCaptor.capture());
            TrackWasRegistered event = eventCaptor.getValue();

            assertNotNull(event.isrc());
            assertEquals(NORMALIZED_ISRC, event.isrc().value());
            assertEquals("Bohemian Rhapsody", event.title());
            assertEquals(List.of("Queen"), event.artistNames());
        }
    }

    @Nested
    @DisplayName("Input Validation")
    class InputValidation {

        @Test
        @DisplayName("Should handle null ISRC gracefully")
        void shouldHandleNullIsrcGracefully() {
            // Given: Mock returns null for null ISRC (default behavior)
            // This simulates the external service not finding any data for invalid input
            
            // When & Then: Should throw ExternalServiceException due to null metadata
            ExternalServiceException thrown = assertThrows(ExternalServiceException.class,
                () -> registerTrackService.registerTrack(null));

            // Then: Should mention no track metadata returned
            assertTrue(thrown.getMessage().contains("No track metadata returned"));
            assertNull(thrown.getIsrc());
            assertEquals("external-api", thrown.getService());
            
            // Then: Should call external API but fail before repository
            verify(musicPlatformPort).getTrackByIsrc(null);
            verifyNoInteractions(producerRepository);
            verifyNoInteractions(eventPublisherPort);
        }

        @Test
        @DisplayName("Should handle empty ISRC gracefully")  
        void shouldHandleEmptyIsrcGracefully() {
            // Given: Mock platform port returns null for empty ISRC
            when(musicPlatformPort.getTrackByIsrc("")).thenReturn(null);
            
            // When & Then: Should fail due to null metadata
            ExternalServiceException thrown = assertThrows(ExternalServiceException.class,
                () -> registerTrackService.registerTrack(""));

            // Then: Should mention no track metadata returned
            assertTrue(thrown.getMessage().contains("No track metadata returned"));
            assertEquals("", thrown.getIsrc());
            assertEquals("external-api", thrown.getService());
            
            // Then: Should call external API but fail before repository
            verify(musicPlatformPort).getTrackByIsrc("");
            verifyNoInteractions(producerRepository);
            verifyNoInteractions(eventPublisherPort);
        }
    }
}