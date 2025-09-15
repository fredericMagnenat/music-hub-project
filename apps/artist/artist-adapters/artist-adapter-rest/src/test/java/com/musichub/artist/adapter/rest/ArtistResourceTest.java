package com.musichub.artist.adapter.rest;

import com.musichub.artist.adapter.rest.service.ProducerAssemblyService;
import com.musichub.artist.application.ports.out.ArtistRepository;
import com.musichub.artist.domain.model.Artist;
import com.musichub.artist.domain.model.ArtistStatus;
import com.musichub.artist.domain.values.Contribution;
import com.musichub.shared.domain.id.ArtistId;
import com.musichub.shared.domain.id.TrackId;
import com.musichub.shared.domain.values.ISRC;
import com.musichub.shared.domain.values.Source;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ArtistResource Unit Tests")
class ArtistResourceTest {

    private ArtistResource artistResource;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private ProducerAssemblyService producerAssemblyService;

    @BeforeEach
    void setUp() {
        artistResource = new ArtistResource(artistRepository, producerAssemblyService);
    }

    @Nested
    @DisplayName("Get Artist by ID")
    class GetArtistById {

        @Test
        @DisplayName("Should return artist when found by ID")
        void shouldReturnArtistWhenFoundById() {
            // Given
            UUID artistUuid = UUID.randomUUID();
            ArtistId artistId = new ArtistId(artistUuid);
            Artist artist = createTestArtist(artistId, "The Beatles");

            List<UUID> producerIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());

            when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));
            when(producerAssemblyService.getProducerIds(artist)).thenReturn(producerIds);

            // When
            Response response = artistResource.getArtistById(artistUuid);

            // Then
            assertThat(response.getStatus()).isEqualTo(200);
            verify(artistRepository).findById(artistId);
            verify(producerAssemblyService).getProducerIds(artist);
        }

        @Test
        @DisplayName("Should return 404 when artist not found by ID")
        void shouldReturn404WhenArtistNotFoundById() {
            // Given
            UUID artistUuid = UUID.randomUUID();
            ArtistId artistId = new ArtistId(artistUuid);

            when(artistRepository.findById(artistId)).thenReturn(Optional.empty());

            // When
            Response response = artistResource.getArtistById(artistUuid);

            // Then
            assertThat(response.getStatus()).isEqualTo(404);
            verify(artistRepository).findById(artistId);
            verify(producerAssemblyService, never()).getProducerIds(any());
        }

        @Test
        @DisplayName("Should return proper error message when artist not found")
        void shouldReturnProperErrorMessageWhenArtistNotFound() {
            // Given
            UUID artistUuid = UUID.randomUUID();
            ArtistId artistId = new ArtistId(artistUuid);

            when(artistRepository.findById(artistId)).thenReturn(Optional.empty());

            // When
            Response response = artistResource.getArtistById(artistUuid);

            // Then
            assertThat(response.getStatus()).isEqualTo(404);
            assertThat(response.getEntity()).isEqualTo("{\"error\":\"Artist not found\"}");
        }
    }

    @Nested
    @DisplayName("Search Artist by Name")
    class SearchArtistByName {

        @Test
        @DisplayName("Should return artist when found by name")
        void shouldReturnArtistWhenFoundByName() {
            // Given
            String artistName = "The Rolling Stones";
            Artist artist = createTestArtist(new ArtistId(UUID.randomUUID()), artistName);

            List<UUID> producerIds = Arrays.asList(UUID.randomUUID());

            when(artistRepository.findByName(artistName)).thenReturn(Optional.of(artist));
            when(producerAssemblyService.getProducerIds(artist)).thenReturn(producerIds);

            // When
            Response response = artistResource.searchArtistByName(artistName);

            // Then
            assertThat(response.getStatus()).isEqualTo(200);
            verify(artistRepository).findByName(artistName);
            verify(producerAssemblyService).getProducerIds(artist);
        }

        @Test
        @DisplayName("Should return 404 when artist not found by name")
        void shouldReturn404WhenArtistNotFoundByName() {
            // Given
            String artistName = "Unknown Artist";

            when(artistRepository.findByName(artistName)).thenReturn(Optional.empty());

            // When
            Response response = artistResource.searchArtistByName(artistName);

            // Then
            assertThat(response.getStatus()).isEqualTo(404);
            verify(artistRepository).findByName(artistName);
            verify(producerAssemblyService, never()).getProducerIds(any());
        }

        @Test
        @DisplayName("Should return 400 when name parameter is null")
        void shouldReturn400WhenNameParameterIsNull() {
            // Given
            String artistName = null;

            // When
            Response response = artistResource.searchArtistByName(artistName);

            // Then
            assertThat(response.getStatus()).isEqualTo(400);
            assertThat(response.getEntity()).isEqualTo("{\"error\":\"Name parameter is required\"}");
            verify(artistRepository, never()).findByName(any());
        }

        @Test
        @DisplayName("Should return 400 when name parameter is empty")
        void shouldReturn400WhenNameParameterIsEmpty() {
            // Given
            String artistName = "";

            // When
            Response response = artistResource.searchArtistByName(artistName);

            // Then
            assertThat(response.getStatus()).isEqualTo(400);
            assertThat(response.getEntity()).isEqualTo("{\"error\":\"Name parameter is required\"}");
            verify(artistRepository, never()).findByName(any());
        }

        @Test
        @DisplayName("Should return 400 when name parameter is only whitespace")
        void shouldReturn400WhenNameParameterIsOnlyWhitespace() {
            // Given
            String artistName = "   ";

            // When
            Response response = artistResource.searchArtistByName(artistName);

            // Then
            assertThat(response.getStatus()).isEqualTo(400);
            verify(artistRepository, never()).findByName(any());
        }

        @Test
        @DisplayName("Should trim whitespace from name parameter")
        void shouldTrimWhitespaceFromNameParameter() {
            // Given
            String artistNameWithSpaces = "  Pink Floyd  ";
            String trimmedName = "Pink Floyd";
            Artist artist = createTestArtist(new ArtistId(UUID.randomUUID()), trimmedName);

            List<UUID> producerIds = Arrays.asList(UUID.randomUUID());

            when(artistRepository.findByName(trimmedName)).thenReturn(Optional.of(artist));
            when(producerAssemblyService.getProducerIds(artist)).thenReturn(producerIds);

            // When
            Response response = artistResource.searchArtistByName(artistNameWithSpaces);

            // Then
            assertThat(response.getStatus()).isEqualTo(200);
            verify(artistRepository).findByName(trimmedName);
        }
    }

    @Nested
    @DisplayName("Response Mapping")
    class ResponseMapping {

        @Test
        @DisplayName("Should properly map artist to response with producer IDs")
        void shouldProperlyMapArtistToResponseWithProducerIds() {
            // Given
            UUID artistUuid = UUID.randomUUID();
            ArtistId artistId = new ArtistId(artistUuid);
            Artist artist = createCompleteTestArtist(artistId, "Complete Artist");

            UUID producerId1 = UUID.randomUUID();
            UUID producerId2 = UUID.randomUUID();
            List<UUID> producerIds = Arrays.asList(producerId1, producerId2);

            when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));
            when(producerAssemblyService.getProducerIds(artist)).thenReturn(producerIds);

            // When
            Response response = artistResource.getArtistById(artistUuid);

            // Then
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getEntity()).isNotNull();

            // Verify the mapper was called with correct parameters
            verify(producerAssemblyService).getProducerIds(artist);
        }

        @Test
        @DisplayName("Should handle empty producer IDs list")
        void shouldHandleEmptyProducerIdsList() {
            // Given
            UUID artistUuid = UUID.randomUUID();
            ArtistId artistId = new ArtistId(artistUuid);
            Artist artist = createTestArtist(artistId, "Solo Artist");

            List<UUID> emptyProducerIds = Arrays.asList();

            when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));
            when(producerAssemblyService.getProducerIds(artist)).thenReturn(emptyProducerIds);

            // When
            Response response = artistResource.getArtistById(artistUuid);

            // Then
            assertThat(response.getStatus()).isEqualTo(200);
            verify(producerAssemblyService).getProducerIds(artist);
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandling {

        @Test
        @DisplayName("Should handle repository exceptions gracefully")
        void shouldHandleRepositoryExceptionsGracefully() {
            // Given
            UUID artistUuid = UUID.randomUUID();
            ArtistId artistId = new ArtistId(artistUuid);

            when(artistRepository.findById(artistId)).thenThrow(new RuntimeException("Database error"));

            // When & Then
            assertThatThrownBy(() -> artistResource.getArtistById(artistUuid))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database error");
        }

        @Test
        @DisplayName("Should handle producer assembly service exceptions gracefully")
        void shouldHandleProducerAssemblyServiceExceptionsGracefully() {
            // Given
            UUID artistUuid = UUID.randomUUID();
            ArtistId artistId = new ArtistId(artistUuid);
            Artist artist = createTestArtist(artistId, "Test Artist");

            when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));
            when(producerAssemblyService.getProducerIds(artist))
                .thenThrow(new RuntimeException("Producer assembly error"));

            // When & Then
            assertThatThrownBy(() -> artistResource.getArtistById(artistUuid))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Producer assembly error");
        }
    }

    private Artist createTestArtist(ArtistId artistId, String name) {
        return Artist.from(artistId, name, ArtistStatus.PROVISIONAL);
    }

    private Artist createCompleteTestArtist(ArtistId artistId, String name) {
        Artist artist = Artist.from(artistId, name, ArtistStatus.VERIFIED);

        // Add contribution
        Contribution contribution = Contribution.of(
            new TrackId(UUID.randomUUID()),
            "Test Track",
            ISRC.of("USRC17607839")
        );
        artist = artist.addContribution(contribution);

        // Add source
        Source source = Source.of("SPOTIFY", "spotify123");
        artist = artist.addSource(source);

        return artist;
    }
}