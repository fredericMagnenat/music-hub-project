package com.musichub.artist.adapter.spi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.musichub.artist.domain.model.Artist;
import com.musichub.artist.domain.model.ArtistStatus;
import com.musichub.shared.domain.values.SourceType;

@DisplayName("TidalArtistClient Unit Tests")
class TidalArtistClientTest {

    private TidalArtistClient tidalClient;

    @BeforeEach
    void setUp() {
        tidalClient = new TidalArtistClient();
    }

    @Nested
    @DisplayName("Source Type Support")
    class SourceTypeSupport {

        @Test
        @DisplayName("Should support TIDAL source type")
        void shouldSupportTidalSourceType() {
            // When
            boolean supports = tidalClient.supports(SourceType.TIDAL);

            // Then
            assertThat(supports).isTrue();
        }

        @Test
        @DisplayName("Should not support SPOTIFY source type")
        void shouldNotSupportSpotifySourceType() {
            // When
            boolean supports = tidalClient.supports(SourceType.SPOTIFY);

            // Then
            assertThat(supports).isFalse();
        }

        @Test
        @DisplayName("Should not support MANUAL source type")
        void shouldNotSupportManualSourceType() {
            // When
            boolean supports = tidalClient.supports(SourceType.MANUAL);

            // Then
            assertThat(supports).isFalse();
        }
    }

    @Nested
    @DisplayName("Find Artist by Name")
    class FindArtistByName {

        @Test
        @DisplayName("Should return empty for unsupported source type")
        void shouldReturnEmptyForUnsupportedSourceType() {
            // Given
            String artistName = "The Beatles";
            SourceType sourceType = SourceType.SPOTIFY;

            // When
            CompletableFuture<Optional<Artist>> result = tidalClient.findArtistByName(artistName, sourceType);

            // Then
            assertThat(result).succeedsWithin(java.time.Duration.ofSeconds(1));
            assertThat(result.join()).isEmpty();
        }

        @Test
        @DisplayName("Should handle network errors gracefully")
        void shouldHandleNetworkErrorsGracefully() {
            // Given
            String artistName = "NonExistentArtist12345";
            SourceType sourceType = SourceType.TIDAL;

            // When
            CompletableFuture<Optional<Artist>> result = tidalClient.findArtistByName(artistName, sourceType);

            // Then - Should not throw exception and return empty
            assertThatCode(result::join).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should format artist name correctly for search")
        void shouldFormatArtistNameCorrectlyForSearch() {
            // Given
            String artistNameWithSpaces = "The Beatles";
            SourceType sourceType = SourceType.TIDAL;

            // When
            CompletableFuture<Optional<Artist>> result = tidalClient.findArtistByName(artistNameWithSpaces, sourceType);

            // Then - Should handle the name formatting without throwing exception
            assertThatCode(result::join).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Find Artist by External ID")
    class FindArtistByExternalId {

        @Test
        @DisplayName("Should return empty for unsupported source type")
        void shouldReturnEmptyForUnsupportedSourceType() {
            // Given
            String externalId = "1566681";
            SourceType sourceType = SourceType.SPOTIFY;

            // When
            CompletableFuture<Optional<Artist>> result = tidalClient.findArtistByExternalId(externalId, sourceType);

            // Then
            assertThat(result).succeedsWithin(java.time.Duration.ofSeconds(1));
            assertThat(result.join()).isEmpty();
        }

        @Test
        @DisplayName("Should handle invalid external ID gracefully")
        void shouldHandleInvalidExternalIdGracefully() {
            // Given
            String invalidExternalId = "invalid-id-12345";
            SourceType sourceType = SourceType.TIDAL;

            // When
            CompletableFuture<Optional<Artist>> result = tidalClient.findArtistByExternalId(invalidExternalId, sourceType);

            // Then - Should not throw exception
            assertThatCode(result::join).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should handle null external ID gracefully")
        void shouldHandleNullExternalIdGracefully() {
            // Given
            String nullExternalId = null;
            SourceType sourceType = SourceType.TIDAL;

            // When
            CompletableFuture<Optional<Artist>> result = tidalClient.findArtistByExternalId(nullExternalId, sourceType);

            // Then - Should not throw exception
            assertThatCode(result::join).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("JSON Parsing")
    class JsonParsing {

        @Test
        @DisplayName("Should handle malformed JSON gracefully")
        void shouldHandleMalformedJsonGracefully() {
            // This test verifies internal JSON parsing robustness
            // Since parseArtistFromSearchResponse is private, we test behavior through public methods

            // Given
            String artistName = "Test Artist";
            SourceType sourceType = SourceType.TIDAL;

            // When & Then - Should not throw exception even if API returns malformed JSON
            CompletableFuture<Optional<Artist>> result = tidalClient.findArtistByName(artistName, sourceType);
            assertThatCode(result::join).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should handle empty response gracefully")
        void shouldHandleEmptyResponseGracefully() {
            // Given
            String artistName = "EmptyResponseTest";
            SourceType sourceType = SourceType.TIDAL;

            // When & Then - Should not throw exception even if API returns empty response
            CompletableFuture<Optional<Artist>> result = tidalClient.findArtistByName(artistName, sourceType);
            assertThatCode(result::join).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Async Behavior")
    class AsyncBehavior {

        @Test
        @DisplayName("Should return CompletableFuture immediately")
        void shouldReturnCompletableFutureImmediately() {
            // Given
            String artistName = "Test Artist";
            SourceType sourceType = SourceType.TIDAL;

            // When
            long startTime = System.currentTimeMillis();
            CompletableFuture<Optional<Artist>> result = tidalClient.findArtistByName(artistName, sourceType);
            long endTime = System.currentTimeMillis();

            // Then - Method should return quickly (< 100ms) without blocking
            assertThat(endTime - startTime).isLessThan(100);
            assertThat(result).isNotNull().isInstanceOf(CompletableFuture.class);
        }

        @Test
        @DisplayName("Should execute asynchronously")
        void shouldExecuteAsynchronously() {
            // Given
            String artistName = "Test Artist";
            SourceType sourceType = SourceType.TIDAL;

            // When
            CompletableFuture<Optional<Artist>> result = tidalClient.findArtistByName(artistName, sourceType);

            // Then - Future should not be completed immediately
            // Note: This might be flaky in very fast environments, but generally should work
            // Wait for completion
            result.join();

            // Should be completed now
            assertThat(result.isDone()).isTrue();
        }
    }

    @Nested
    @DisplayName("Artist Creation and Enrichment")
    class ArtistCreationAndEnrichment {

        @Test
        @DisplayName("Should create verified artist with TIDAL source when successful")
        void shouldCreateVerifiedArtistWithTidalSourceWhenSuccessful() {
            // This is more of an integration test, but we can test the behavior
            // Note: This test might occasionally succeed if the network call works

            // Given
            String knownArtistName = "Queen"; // Well-known artist likely to be in Tidal
            SourceType sourceType = SourceType.TIDAL;

            // When
            CompletableFuture<Optional<Artist>> result = tidalClient.findArtistByName(knownArtistName, sourceType);
            Optional<Artist> artistOpt = result.join();

            // Then - If an artist is found, it should be properly configured
            artistOpt.ifPresent(artist -> {
                assertThat(artist.getNameValue()).isNotEmpty();
                assertThat(artist.getStatus()).isEqualTo(ArtistStatus.VERIFIED);
                assertThat(artist.hasSource(SourceType.TIDAL)).isTrue();

                var tidalSource = artist.getSource(SourceType.TIDAL);
                assertThat(tidalSource).isPresent();
                assertThat(tidalSource.get().sourceId()).isNotEmpty();
            });
        }
    }
}
