package com.musichub.artist.application.service;

import com.musichub.artist.application.ports.out.ArtistReconciliationPort;
import com.musichub.artist.application.ports.out.ArtistRepository;
import com.musichub.artist.domain.model.Artist;
import com.musichub.artist.domain.model.ArtistStatus;
import com.musichub.shared.domain.values.Source;
import com.musichub.shared.domain.values.SourceType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ArtistEnrichmentService Unit Tests")
class ArtistEnrichmentServiceTest {

    private ArtistEnrichmentService enrichmentService;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private ArtistReconciliationPort tidalPort;

    @Mock
    private ArtistReconciliationPort spotifyPort;

    @BeforeEach
    void setUp() {
        List<ArtistReconciliationPort> reconciliationPorts = Arrays.asList(tidalPort, spotifyPort);
        enrichmentService = new ArtistEnrichmentService(artistRepository, reconciliationPorts);

        // Setup default port support - only stub what's actually used in tests
        lenient().when(tidalPort.supports(SourceType.TIDAL)).thenReturn(true);
        lenient().when(tidalPort.supports(any())).thenReturn(false);
        lenient().when(spotifyPort.supports(SourceType.SPOTIFY)).thenReturn(true);
        lenient().when(spotifyPort.supports(any())).thenReturn(false);
    }

    @Nested
    @DisplayName("Enrich Artist")
    class EnrichArtist {

        @Test
        @DisplayName("Should return same artist when already verified")
        void shouldReturnSameArtistWhenAlreadyVerified() {
            // Given
            Artist verifiedArtist = Artist.createProvisional("The Beatles")
                .markAsVerified();

            // When
            CompletableFuture<Artist> result = enrichmentService.enrichArtist(verifiedArtist);

            // Then
            assertThat(result).succeedsWithin(java.time.Duration.ofSeconds(1));
            assertThat(result.join()).isSameAs(verifiedArtist);
            verify(artistRepository, never()).save(any());
            verify(tidalPort, never()).findArtistByName(any(), any());
            verify(spotifyPort, never()).findArtistByName(any(), any());
        }

        @Test
        @DisplayName("Should enrich provisional artist with TIDAL data when found")
        void shouldEnrichProvisionalArtistWithTidalDataWhenFound() {
            // Given
            Artist provisionalArtist = Artist.createProvisional("Queen");

            Artist tidalArtist = Artist.createProvisional("Queen")
                .addSource(Source.of("TIDAL", "1566681"))
                .markAsVerified();

            Artist enrichedArtist = provisionalArtist
                .addSource(Source.of("TIDAL", "1566681"))
                .markAsVerified();

            when(tidalPort.findArtistByName("Queen", SourceType.TIDAL))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(tidalArtist)));
            when(artistRepository.save(any(Artist.class))).thenReturn(enrichedArtist);

            // When
            CompletableFuture<Artist> result = enrichmentService.enrichArtist(provisionalArtist);

            // Then
            assertThat(result).succeedsWithin(java.time.Duration.ofSeconds(1));
            Artist resultArtist = result.join();

            assertThat(resultArtist.getStatus()).isEqualTo(ArtistStatus.VERIFIED);
            assertThat(resultArtist.hasSource(SourceType.TIDAL)).isTrue();

            verify(tidalPort).findArtistByName("Queen", SourceType.TIDAL);
            verify(artistRepository).save(any(Artist.class));
        }

        @Test
        @DisplayName("Should try Spotify when TIDAL not found and follow hierarchy")
        void shouldTrySpotifyWhenTidalNotFoundAndFollowHierarchy() {
            // Given
            Artist provisionalArtist = Artist.createProvisional("Radiohead");

            Artist spotifyArtist = Artist.createProvisional("Radiohead")
                .addSource(Source.of("SPOTIFY", "4Z8W4fKeB5YxbusRsdQVPb"))
                .markAsVerified();

            Artist enrichedArtist = provisionalArtist
                .addSource(Source.of("SPOTIFY", "4Z8W4fKeB5YxbusRsdQVPb"))
                .markAsVerified();

            // TIDAL returns empty, Spotify returns artist
            when(tidalPort.findArtistByName("Radiohead", SourceType.TIDAL))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));
            when(spotifyPort.findArtistByName("Radiohead", SourceType.SPOTIFY))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(spotifyArtist)));
            when(artistRepository.save(any(Artist.class))).thenReturn(enrichedArtist);

            // When
            CompletableFuture<Artist> result = enrichmentService.enrichArtist(provisionalArtist);

            // Then
            assertThat(result).succeedsWithin(java.time.Duration.ofSeconds(1));
            Artist resultArtist = result.join();

            assertThat(resultArtist.getStatus()).isEqualTo(ArtistStatus.VERIFIED);
            assertThat(resultArtist.hasSource(SourceType.SPOTIFY)).isTrue();

            verify(tidalPort).findArtistByName("Radiohead", SourceType.TIDAL);
            verify(spotifyPort).findArtistByName("Radiohead", SourceType.SPOTIFY);
            verify(artistRepository).save(any(Artist.class));
        }

        @Test
        @DisplayName("Should return original artist when no external sources found")
        void shouldReturnOriginalArtistWhenNoExternalSourcesFound() {
            // Given
            Artist provisionalArtist = Artist.createProvisional("Unknown Artist");

            when(tidalPort.findArtistByName("Unknown Artist", SourceType.TIDAL))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));
            when(spotifyPort.findArtistByName("Unknown Artist", SourceType.SPOTIFY))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

            // When
            CompletableFuture<Artist> result = enrichmentService.enrichArtist(provisionalArtist);

            // Then
            assertThat(result).succeedsWithin(java.time.Duration.ofSeconds(1));
            Artist resultArtist = result.join();

            assertThat(resultArtist).isSameAs(provisionalArtist);
            assertThat(resultArtist.getStatus()).isEqualTo(ArtistStatus.PROVISIONAL);

            verify(tidalPort).findArtistByName("Unknown Artist", SourceType.TIDAL);
            verify(spotifyPort).findArtistByName("Unknown Artist", SourceType.SPOTIFY);
            verify(artistRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should prioritize TIDAL over Spotify when both found")
        void shouldPrioritizeTidalOverSpotifyWhenBothFound() {
            // Given
            Artist provisionalArtist = Artist.createProvisional("Pink Floyd");

            Artist tidalArtist = Artist.createProvisional("Pink Floyd")
                .addSource(Source.of("TIDAL", "7804"))
                .markAsVerified();

            Artist enrichedArtist = provisionalArtist
                .addSource(Source.of("TIDAL", "7804"))
                .markAsVerified();

            when(tidalPort.findArtistByName("Pink Floyd", SourceType.TIDAL))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(tidalArtist)));
            when(artistRepository.save(any(Artist.class))).thenReturn(enrichedArtist);

            // When
            CompletableFuture<Artist> result = enrichmentService.enrichArtist(provisionalArtist);

            // Then
            assertThat(result).succeedsWithin(java.time.Duration.ofSeconds(1));
            Artist resultArtist = result.join();

            assertThat(resultArtist.hasSource(SourceType.TIDAL)).isTrue();

            verify(tidalPort).findArtistByName("Pink Floyd", SourceType.TIDAL);
            // Spotify should not be called since TIDAL found the artist
            verify(spotifyPort, never()).findArtistByName(any(), any());
            verify(artistRepository).save(any(Artist.class));
        }
    }

    @Nested
    @DisplayName("Source Hierarchy")
    class SourceHierarchy {

        @Test
        @DisplayName("Should respect source hierarchy when searching")
        void shouldRespectSourceHierarchyWhenSearching() {
            // Given
            Artist provisionalArtist = Artist.createProvisional("Test Artist");

            // Setup multiple ports but only one supports each source type
            when(tidalPort.findArtistByName("Test Artist", SourceType.TIDAL))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

            Artist spotifyArtist = Artist.createProvisional("Test Artist")
                .addSource(Source.of("SPOTIFY", "spotify123"))
                .markAsVerified();

            when(spotifyPort.findArtistByName("Test Artist", SourceType.SPOTIFY))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(spotifyArtist)));

            Artist enrichedArtist = provisionalArtist
                .addSource(Source.of("SPOTIFY", "spotify123"))
                .markAsVerified();

            when(artistRepository.save(any(Artist.class))).thenReturn(enrichedArtist);

            // When
            CompletableFuture<Artist> result = enrichmentService.enrichArtist(provisionalArtist);

            // Then
            assertThat(result).succeedsWithin(java.time.Duration.ofSeconds(1));

            // Should try TIDAL first (higher priority), then fall back to Spotify
            verify(tidalPort).findArtistByName("Test Artist", SourceType.TIDAL);
            verify(spotifyPort).findArtistByName("Test Artist", SourceType.SPOTIFY);
        }

        @Test
        @DisplayName("Should handle unsupported source types gracefully")
        void shouldHandleUnsupportedSourceTypesGracefully() {
            // Given
            Artist provisionalArtist = Artist.createProvisional("Test Artist");

            // No ports support any source types
            when(tidalPort.supports(any())).thenReturn(false);
            when(spotifyPort.supports(any())).thenReturn(false);

            // When
            CompletableFuture<Artist> result = enrichmentService.enrichArtist(provisionalArtist);

            // Then
            assertThat(result).succeedsWithin(java.time.Duration.ofSeconds(1));
            Artist resultArtist = result.join();

            assertThat(resultArtist).isSameAs(provisionalArtist);
            verify(artistRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Artist Data Merging")
    class ArtistDataMerging {

        @Test
        @DisplayName("Should merge sources from external artist")
        void shouldMergeSourcesFromExternalArtist() {
            // Given
            Artist provisionalArtist = Artist.createProvisional("Merge Test");

            Artist externalArtist = Artist.createProvisional("Merge Test")
                .addSource(Source.of("TIDAL", "tidal123"))
                .addSource(Source.of("SPOTIFY", "spotify456"))
                .markAsVerified();

            when(tidalPort.findArtistByName("Merge Test", SourceType.TIDAL))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(externalArtist)));

            // Capture the artist that gets saved
            when(artistRepository.save(any(Artist.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            CompletableFuture<Artist> result = enrichmentService.enrichArtist(provisionalArtist);

            // Then
            assertThat(result).succeedsWithin(java.time.Duration.ofSeconds(1));
            Artist resultArtist = result.join();

            assertThat(resultArtist.getStatus()).isEqualTo(ArtistStatus.VERIFIED);
            assertThat(resultArtist.hasSource(SourceType.TIDAL)).isTrue();
            assertThat(resultArtist.hasSource(SourceType.SPOTIFY)).isTrue();
        }

        @Test
        @DisplayName("Should preserve existing contributions when merging")
        void shouldPreserveExistingContributionsWhenMerging() {
            // Given
            Artist provisionalArtist = Artist.createProvisional("Contributions Test");
            // Assume artist already has contributions (from previous track registrations)

            Artist externalArtist = Artist.createProvisional("Contributions Test")
                .addSource(Source.of("TIDAL", "tidal789"))
                .markAsVerified();

            when(tidalPort.findArtistByName("Contributions Test", SourceType.TIDAL))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(externalArtist)));

            when(artistRepository.save(any(Artist.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            CompletableFuture<Artist> result = enrichmentService.enrichArtist(provisionalArtist);

            // Then
            assertThat(result).succeedsWithin(java.time.Duration.ofSeconds(1));
            Artist resultArtist = result.join();

            // Contributions should be preserved (they come from the original artist)
            assertThat(resultArtist.getContributions()).isEqualTo(provisionalArtist.getContributions());
            assertThat(resultArtist.getStatus()).isEqualTo(ArtistStatus.VERIFIED);
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandling {

        @Test
        @DisplayName("Should handle port exceptions gracefully")
        void shouldHandlePortExceptionsGracefully() {
            // Given
            Artist provisionalArtist = Artist.createProvisional("Error Test");

            when(tidalPort.findArtistByName("Error Test", SourceType.TIDAL))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("API Error")));

            Artist spotifyArtist = Artist.createProvisional("Error Test")
                .addSource(Source.of("SPOTIFY", "spotify999"))
                .markAsVerified();

            when(spotifyPort.findArtistByName("Error Test", SourceType.SPOTIFY))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(spotifyArtist)));

            Artist enrichedArtist = provisionalArtist
                .addSource(Source.of("SPOTIFY", "spotify999"))
                .markAsVerified();

            when(artistRepository.save(any(Artist.class))).thenReturn(enrichedArtist);

            // When
            CompletableFuture<Artist> result = enrichmentService.enrichArtist(provisionalArtist);

            // Then - Should continue to next source despite TIDAL error
            assertThat(result).succeedsWithin(java.time.Duration.ofSeconds(2));
            Artist resultArtist = result.join();

            assertThat(resultArtist.hasSource(SourceType.SPOTIFY)).isTrue();
            verify(spotifyPort).findArtistByName("Error Test", SourceType.SPOTIFY);
        }

        @Test
        @DisplayName("Should handle repository exceptions")
        void shouldHandleRepositoryExceptions() {
            // Given
            Artist provisionalArtist = Artist.createProvisional("Repository Error");

            Artist tidalArtist = Artist.createProvisional("Repository Error")
                .addSource(Source.of("TIDAL", "tidal000"))
                .markAsVerified();

            when(tidalPort.findArtistByName("Repository Error", SourceType.TIDAL))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(tidalArtist)));
            when(artistRepository.save(any(Artist.class)))
                .thenThrow(new RuntimeException("Database error"));

            // When & Then
            CompletableFuture<Artist> result = enrichmentService.enrichArtist(provisionalArtist);

            assertThatThrownBy(() -> result.join())
                .hasCauseInstanceOf(RuntimeException.class);
        }
    }
}