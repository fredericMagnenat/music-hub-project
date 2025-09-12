package com.musichub.producer.adapter.spi;

import com.musichub.producer.adapter.spi.dto.ArtistDto;
import com.musichub.producer.adapter.spi.dto.TrackMetadataDto;
import com.musichub.producer.adapter.spi.exception.TrackNotFoundInExternalServiceException;
import com.musichub.producer.application.dto.ExternalTrackMetadata;
import com.musichub.producer.application.exception.ExternalServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TidalMusicPlatformAdapter.
 * Tests the adapter logic that bridges application ports with Tidal SPI service.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TidalMusicPlatformAdapter")
class TidalMusicPlatformAdapterTest {

    @Mock
    private TidalMusicPlatformService tidalService;

    @InjectMocks
    private TidalMusicPlatformAdapter adapter;

    private static final String TEST_ISRC = "GBUM71507409";
    private static final UUID TEST_ARTIST_ID = UUID.fromString("12345678-1234-1234-1234-123456789abc");

    private ArtistDto createArtist(String name) {
        ArtistDto artist = new ArtistDto();
        artist.name = name;
        artist.id = TEST_ARTIST_ID;
        return artist;
    }

    @Nested
    @DisplayName("Successful Mapping")
    class SuccessfulMapping {

        @Test
        @DisplayName("Should successfully map Tidal DTO to external metadata")
        void shouldSuccessfullyMapTidalDtoToExternalMetadata() {
            // Given: Tidal service returns valid DTO
            TrackMetadataDto tidalDto = new TrackMetadataDto(
                TEST_ISRC,
                "Bohemian Rhapsody",
                List.of(createArtist("Queen")),
                "TIDAL"
            );
            when(tidalService.getTrackByIsrc(TEST_ISRC)).thenReturn(tidalDto);

            // When: Getting track metadata
            ExternalTrackMetadata result = adapter.getTrackByIsrc(TEST_ISRC);

            // Then: Should correctly map all fields
            assertNotNull(result);
            assertEquals(TEST_ISRC, result.getIsrc());
            assertEquals("Bohemian Rhapsody", result.getTitle());
            assertEquals(List.of("Queen"), result.getArtistNames());
            assertEquals("TIDAL", result.getPlatform());

            // Then: Should call Tidal service once
            verify(tidalService).getTrackByIsrc(TEST_ISRC);
        }

        @Test
        @DisplayName("Should handle multiple artists correctly")
        void shouldHandleMultipleArtistsCorrectly() {
            // Given: Tidal DTO with multiple artists
            TrackMetadataDto tidalDto = new TrackMetadataDto(
                TEST_ISRC,
                "Under Pressure",
                List.of(createArtist("Queen"), createArtist("David Bowie")),
                "TIDAL"
            );
            when(tidalService.getTrackByIsrc(TEST_ISRC)).thenReturn(tidalDto);

            // When: Getting track metadata
            ExternalTrackMetadata result = adapter.getTrackByIsrc(TEST_ISRC);

            // Then: Should preserve all artists
            assertEquals(List.of("Queen", "David Bowie"), result.getArtistNames());
        }

        @Test
        @DisplayName("Should handle single artist correctly")
        void shouldHandleSingleArtistCorrectly() {
            // Given: Tidal DTO with single artist
            TrackMetadataDto tidalDto = new TrackMetadataDto(
                TEST_ISRC,
                "Imagine",
                List.of(createArtist("John Lennon")),
                "TIDAL"
            );
            when(tidalService.getTrackByIsrc(TEST_ISRC)).thenReturn(tidalDto);

            // When: Getting track metadata
            ExternalTrackMetadata result = adapter.getTrackByIsrc(TEST_ISRC);

            // Then: Should handle single artist
            assertEquals(List.of("John Lennon"), result.getArtistNames());
            assertEquals("John Lennon", result.getArtistNames().get(0));
        }
    }

    @Nested
    @DisplayName("Exception Handling")
    class ExceptionHandling {

        @Test
        @DisplayName("Should convert TrackNotFoundInExternalServiceException to ExternalServiceException")
        void shouldConvertTrackNotFoundToExternalServiceException() {
            // Given: Tidal service throws TrackNotFoundInExternalServiceException
            TrackNotFoundInExternalServiceException tidalException = 
                new TrackNotFoundInExternalServiceException(
                    "Track not found in Tidal", TEST_ISRC, "tidal-api"
                );
            when(tidalService.getTrackByIsrc(TEST_ISRC)).thenThrow(tidalException);

            // When & Then: Should convert to ExternalServiceException
            ExternalServiceException thrown = assertThrows(
                ExternalServiceException.class,
                () -> adapter.getTrackByIsrc(TEST_ISRC)
            );

            // Then: Should preserve error information
            assertTrue(thrown.getMessage().contains("Track not found in Tidal service"));
            assertEquals(TEST_ISRC, thrown.getIsrc());
            assertEquals("tidal", thrown.getService());
            assertEquals(tidalException, thrown.getCause());

            // Then: Should call Tidal service once
            verify(tidalService).getTrackByIsrc(TEST_ISRC);
        }

        @Test
        @DisplayName("Should convert unexpected exceptions to ExternalServiceException")
        void shouldConvertUnexpectedExceptionsToExternalServiceException() {
            // Given: Tidal service throws unexpected exception
            RuntimeException unexpectedException = new RuntimeException("Network timeout");
            when(tidalService.getTrackByIsrc(TEST_ISRC)).thenThrow(unexpectedException);

            // When & Then: Should convert to ExternalServiceException
            ExternalServiceException thrown = assertThrows(
                ExternalServiceException.class,
                () -> adapter.getTrackByIsrc(TEST_ISRC)
            );

            // Then: Should wrap unexpected error
            assertTrue(thrown.getMessage().contains("Unexpected error calling Tidal service"));
            assertEquals(TEST_ISRC, thrown.getIsrc());
            assertEquals("tidal", thrown.getService());
            assertEquals(unexpectedException, thrown.getCause());

            // Then: Should call Tidal service once
            verify(tidalService).getTrackByIsrc(TEST_ISRC);
        }

        @Test
        @DisplayName("Should handle null response from Tidal service")
        void shouldHandleNullResponseFromTidalService() {
            // Given: Tidal service returns null
            when(tidalService.getTrackByIsrc(TEST_ISRC)).thenReturn(null);

            // When & Then: Should wrap NullPointerException in ExternalServiceException
            ExternalServiceException thrown = assertThrows(ExternalServiceException.class,
                () -> adapter.getTrackByIsrc(TEST_ISRC));

            // Then: Should have proper error details
            assertEquals(TEST_ISRC, thrown.getIsrc());
            assertEquals("tidal", thrown.getService());
            assertTrue(thrown.getMessage().contains("Unexpected error"));
            assertNotNull(thrown.getCause());
            assertTrue(thrown.getCause() instanceof NullPointerException);

            verify(tidalService).getTrackByIsrc(TEST_ISRC);
        }
    }

    @Nested
    @DisplayName("Data Integrity")
    class DataIntegrity {

        @Test
        @DisplayName("Should preserve ISRC exactly as received from Tidal")
        void shouldPreserveIsrcExactlyAsReceivedFromTidal() {
            // Given: Tidal DTO with formatted ISRC
            String formattedIsrc = "GB-UM7-15-07409";
            TrackMetadataDto tidalDto = new TrackMetadataDto(
                formattedIsrc,
                "Test Track",
                List.of(createArtist("Test Artist")),
                "TIDAL"
            );
            when(tidalService.getTrackByIsrc(TEST_ISRC)).thenReturn(tidalDto);

            // When: Getting track metadata
            ExternalTrackMetadata result = adapter.getTrackByIsrc(TEST_ISRC);

            // Then: Should preserve ISRC format from Tidal
            assertEquals(formattedIsrc, result.getIsrc());
        }

        @Test
        @DisplayName("Should preserve title exactly as received from Tidal")
        void shouldPreserveTitleExactlyAsReceivedFromTidal() {
            // Given: Tidal DTO with special characters in title
            String complexTitle = "Bohemian Rhapsody (2011 Remaster) - Single Version";
            TrackMetadataDto tidalDto = new TrackMetadataDto(
                TEST_ISRC,
                complexTitle,
                List.of(createArtist("Queen")),
                "TIDAL"
            );
            when(tidalService.getTrackByIsrc(TEST_ISRC)).thenReturn(tidalDto);

            // When: Getting track metadata
            ExternalTrackMetadata result = adapter.getTrackByIsrc(TEST_ISRC);

            // Then: Should preserve complex title
            assertEquals(complexTitle, result.getTitle());
        }

        @Test
        @DisplayName("Should preserve platform name exactly as received from Tidal")
        void shouldPreservePlatformNameExactlyAsReceivedFromTidal() {
            // Given: Tidal DTO with specific platform name
            TrackMetadataDto tidalDto = new TrackMetadataDto(
                TEST_ISRC,
                "Test Track",
                List.of(createArtist("Test Artist")),
                "TIDAL_PREMIUM"
            );
            when(tidalService.getTrackByIsrc(TEST_ISRC)).thenReturn(tidalDto);

            // When: Getting track metadata
            ExternalTrackMetadata result = adapter.getTrackByIsrc(TEST_ISRC);

            // Then: Should preserve platform name
            assertEquals("TIDAL_PREMIUM", result.getPlatform());
        }

        @Test
        @DisplayName("Should handle empty artist list")
        void shouldHandleEmptyArtistList() {
            // Given: Tidal DTO with empty artist list
            TrackMetadataDto tidalDto = new TrackMetadataDto(
                TEST_ISRC,
                "Instrumental Track",
                List.of(),
                "TIDAL"
            );
            when(tidalService.getTrackByIsrc(TEST_ISRC)).thenReturn(tidalDto);

            // When: Getting track metadata
            ExternalTrackMetadata result = adapter.getTrackByIsrc(TEST_ISRC);

            // Then: Should handle empty list
            assertTrue(result.getArtistNames().isEmpty());
        }
    }

    @Nested
    @DisplayName("Service Integration")
    class ServiceIntegration {

        @Test
        @DisplayName("Should pass ISRC parameter correctly to Tidal service")
        void shouldPassIsrcParameterCorrectlyToTidalService() {
            // Given: Tidal service setup
            TrackMetadataDto tidalDto = new TrackMetadataDto(
                TEST_ISRC, "Test", List.of(createArtist("Artist")), "TIDAL"
            );
            when(tidalService.getTrackByIsrc(TEST_ISRC)).thenReturn(tidalDto);

            // When: Getting track metadata
            adapter.getTrackByIsrc(TEST_ISRC);

            // Then: Should pass ISRC exactly as provided
            verify(tidalService).getTrackByIsrc(TEST_ISRC);
        }

        @Test
        @DisplayName("Should call Tidal service exactly once per request")
        void shouldCallTidalServiceExactlyOncePerRequest() {
            // Given: Tidal service setup
            TrackMetadataDto tidalDto = new TrackMetadataDto(
                TEST_ISRC, "Test", List.of(createArtist("Artist")), "TIDAL"
            );
            when(tidalService.getTrackByIsrc(TEST_ISRC)).thenReturn(tidalDto);

            // When: Getting track metadata
            adapter.getTrackByIsrc(TEST_ISRC);

            // Then: Should call service exactly once
            verify(tidalService, times(1)).getTrackByIsrc(TEST_ISRC);
        }
    }
}