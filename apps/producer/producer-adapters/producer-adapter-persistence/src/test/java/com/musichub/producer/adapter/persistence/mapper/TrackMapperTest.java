package com.musichub.producer.adapter.persistence.mapper;

import com.musichub.producer.adapter.persistence.entity.TrackEntity;
import com.musichub.producer.domain.model.Track;
import com.musichub.producer.domain.values.Source;
import com.musichub.producer.domain.values.TrackStatus;
import com.musichub.shared.domain.values.ISRC;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TrackMapper Unit Tests")
class TrackMapperTest {

    @Nested
    @DisplayName("toDbo() - Domain to Entity Mapping")
    class ToDboTests {

        @Test
        @DisplayName("Should convert complete Track to TrackEntity with all fields")
        void toDbo_shouldConvertCompleteTrack() {
            // Given
            ISRC isrc = ISRC.of("FRLA12400001");
            String title = "Test Track";
            List<String> artistNames = List.of("Artist 1", "Artist 2");
            List<Source> sources = List.of(
                    Source.of("SPOTIFY", "spotify-123"),
                    Source.of("APPLE_MUSIC", "apple-456")
            );
            TrackStatus status = TrackStatus.PROVISIONAL;

            Track domain = Track.of(isrc, title, artistNames, sources, status);

            // When
            TrackEntity entity = TrackMapper.toDbo(domain);

            // Then
            assertNotNull(entity, "Entity should not be null");
            assertEquals("FRLA12400001", entity.getIsrc(), "ISRC should be mapped correctly");
            assertEquals("Test Track", entity.getTitle(), "Title should be mapped correctly");
            assertEquals("PROVISIONAL", entity.getStatus(), "Status should be mapped as enum name");

            assertEquals(2, entity.getArtistNames().size(), "Should have 2 artists");
            assertTrue(entity.getArtistNames().contains("Artist 1"), "Should contain first artist");
            assertTrue(entity.getArtistNames().contains("Artist 2"), "Should contain second artist");

            assertNotNull(entity.getSources(), "Sources should not be null");
            assertEquals(2, entity.getSources().size(), "Should have 2 sources");
            
            // Verify sources are correctly mapped
            Source spotifySource = entity.getSources().stream()
                    .filter(s -> "SPOTIFY".equals(s.sourceName()))
                    .findFirst()
                    .orElse(null);
            assertNotNull(spotifySource, "Should have Spotify source");
            assertEquals("spotify-123", spotifySource.sourceId(), "Spotify source ID should match");
            
            Source appleSource = entity.getSources().stream()
                    .filter(s -> "APPLE_MUSIC".equals(s.sourceName()))
                    .findFirst()
                    .orElse(null);
            assertNotNull(appleSource, "Should have Apple Music source");
            assertEquals("apple-456", appleSource.sourceId(), "Apple Music source ID should match");
        }

        @Test
        @DisplayName("Should handle null Track gracefully")
        void toDbo_shouldHandleNullTrack() {
            // When
            TrackEntity entity = TrackMapper.toDbo(null);

            // Then
            assertNull(entity, "Should return null for null input");
        }

        @Test
        @DisplayName("Should handle Track with single source and artist")
        void toDbo_shouldHandleSingleSourceAndArtist() {
            // Given
            Track domain = Track.of(
                    ISRC.of("FRLA12400003"),
                    "Single Source Track",
                    List.of("Solo Artist"),
                    List.of(Source.of("MANUAL", "manual-001")),
                    TrackStatus.PROVISIONAL
            );

            // When
            TrackEntity entity = TrackMapper.toDbo(domain);

            // Then
            assertNotNull(entity.getSources());
            assertEquals(1, entity.getSources().size());
            Source manualSource = entity.getSources().get(0);
            assertEquals("MANUAL", manualSource.sourceName());
            assertEquals("manual-001", manualSource.sourceId());

            assertEquals(1, entity.getArtistNames().size());
            assertTrue(entity.getArtistNames().contains("Solo Artist"));
        }
    }

    @Nested
    @DisplayName("toDomain() - Entity to Domain Mapping")
    class ToDomainTests {

        @Test
        @DisplayName("Should convert complete TrackEntity to Track with all fields")
        void toDomain_shouldConvertCompleteEntity() {
            // Given
            TrackEntity entity = new TrackEntity();
            entity.setId(UUID.randomUUID());
            entity.setIsrc("FRLA12400001");
            entity.setTitle("Test Track");
            entity.setStatus("PROVISIONAL");
            entity.setArtistNames(List.of("Artist 1", "Artist 2"));
            entity.setSources(List.of(
                    Source.of("SPOTIFY", "spotify-123"),
                    Source.of("APPLE_MUSIC", "apple-456")
            ));

            // When
            Track domain = TrackMapper.toDomain(entity);

            // Then
            assertNotNull(domain, "Domain should not be null");
            assertEquals(ISRC.of("FRLA12400001"), domain.isrc(), "ISRC should be converted correctly");
            assertEquals("Test Track", domain.title(), "Title should be mapped correctly");
            assertEquals(TrackStatus.PROVISIONAL, domain.status(), "Status should be converted to enum");

            assertEquals(2, domain.artistNames().size(), "Should have 2 artists");
            assertTrue(domain.artistNames().contains("Artist 1"), "Should contain first artist");
            assertTrue(domain.artistNames().contains("Artist 2"), "Should contain second artist");

            assertEquals(2, domain.sources().size(), "Should have 2 sources");

            // Verify sources are correctly deserialized
            Source spotifySource = domain.sources().stream()
                    .filter(s -> "SPOTIFY".equals(s.sourceName()))
                    .findFirst()
                    .orElse(null);
            assertNotNull(spotifySource, "Should have Spotify source");
            assertEquals("spotify-123", spotifySource.sourceId(), "Spotify source ID should match");

            Source appleSource = domain.sources().stream()
                    .filter(s -> "APPLE_MUSIC".equals(s.sourceName()))
                    .findFirst()
                    .orElse(null);
            assertNotNull(appleSource, "Should have Apple Music source");
            assertEquals("apple-456", appleSource.sourceId(), "Apple Music source ID should match");
        }

        @Test
        @DisplayName("Should handle null TrackEntity gracefully")
        void toDomain_shouldHandleNullEntity() {
            // When
            Track domain = TrackMapper.toDomain(null);

            // Then
            assertNull(domain, "Should return null for null input");
        }

        @Test
        @DisplayName("Should handle entity with valid sources")
        void toDomain_shouldHandleValidSources() {
            // Given
            TrackEntity entity = new TrackEntity();
            entity.setId(UUID.randomUUID());
            entity.setIsrc("FRLA12400006");
            entity.setTitle("Valid Sources Track");
            entity.setStatus("PROVISIONAL");
            entity.setArtistNames(List.of("Artist"));
            entity.setSources(List.of(Source.of("SPOTIFY", "test-123")));

            // When
            Track domain = TrackMapper.toDomain(entity);

            // Then
            assertNotNull(domain, "Domain should not be null");
            assertEquals(1, domain.sources().size(), "Should have 1 source");
            assertEquals("SPOTIFY", domain.sources().get(0).sourceName());
            assertEquals("test-123", domain.sources().get(0).sourceId());
        }
    }

    @Nested
    @DisplayName("Domain Business Rules Validation")
    class DomainValidationTests {

        @Test
        @DisplayName("Should fail when trying to create Track with empty artistNames via mapper")
        void toDomain_shouldFailWithEmptyArtistNames() {
            // Given - Entity with empty artistNames (violates domain rule)
            TrackEntity entity = new TrackEntity();
            entity.setId(UUID.randomUUID());
            entity.setIsrc("FRLA12400007");
            entity.setTitle("Empty Artists Track");
            entity.setStatus("PROVISIONAL");
            entity.setArtistNames(List.of()); // Empty list violates domain rule
            entity.setSources(List.of(Source.of("SPOTIFY", "test-123")));

            // When & Then - Should fail due to domain validation
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> TrackMapper.toDomain(entity));
            assertTrue(exception.getMessage().contains("artistNames must not be null or empty"),
                    "Should fail with domain validation message for empty artistNames");
        }

        @Test
        @DisplayName("Should fail when trying to create Track with null artistNames via mapper")
        void toDomain_shouldFailWithNullArtistNames() {
            // Given - Entity with null artistNames (violates domain rule)
            TrackEntity entity = new TrackEntity();
            entity.setId(UUID.randomUUID());
            entity.setIsrc("FRLA12400008");
            entity.setTitle("Null Artists Track");
            entity.setStatus("PROVISIONAL");
            entity.setArtistNames(null); // Null violates domain rule
            entity.setSources(List.of(Source.of("SPOTIFY", "test-123")));

            // When & Then - Should fail due to domain validation
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> TrackMapper.toDomain(entity));
            assertTrue(exception.getMessage().contains("artistNames must not be null or empty"),
                    "Should fail with domain validation message for null artistNames");
        }

        @Test
        @DisplayName("Should fail when trying to create Track with empty sources via mapper")
        void toDomain_shouldFailWithEmptySources() {
            // Given - Entity with empty sources (violates domain rule)
            TrackEntity entity = new TrackEntity();
            entity.setId(UUID.randomUUID());
            entity.setIsrc("FRLA12400009");
            entity.setTitle("Empty Sources Track");
            entity.setStatus("PROVISIONAL");
            entity.setArtistNames(List.of("Artist"));
            entity.setSources(List.of()); // Empty sources violates domain rule

            // When & Then - Should fail due to domain validation
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> TrackMapper.toDomain(entity));
            assertTrue(exception.getMessage().contains("sources must not be null or empty"),
                    "Should fail with domain validation message for empty sources");
        }

        @Test
        @DisplayName("Should fail when trying to create Track with null sources via mapper")
        void toDomain_shouldFailWithNullSources() {
            // Given - Entity with null sources (violates domain rule)
            TrackEntity entity = new TrackEntity();
            entity.setId(UUID.randomUUID());
            entity.setIsrc("FRLA12400010");
            entity.setTitle("Null Sources Track");
            entity.setStatus("PROVISIONAL");
            entity.setArtistNames(List.of("Artist"));
            entity.setSources(null); // Null sources violates domain rule

            // When & Then - Should fail due to domain validation
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> TrackMapper.toDomain(entity));
            assertTrue(exception.getMessage().contains("sources must not be null or empty"),
                    "Should fail with domain validation message for null sources");
        }

        @Test
        @DisplayName("Should fail when trying to create Track with blank title via mapper")
        void toDomain_shouldFailWithBlankTitle() {
            // Given - Entity with blank title (violates domain rule)
            TrackEntity entity = new TrackEntity();
            entity.setId(UUID.randomUUID());
            entity.setIsrc("FRLA12400011");
            entity.setTitle("   "); // Blank title violates domain rule
            entity.setStatus("PROVISIONAL");
            entity.setArtistNames(List.of("Artist"));
            entity.setSources(List.of(Source.of("SPOTIFY", "test-123")));

            // When & Then - Should fail due to domain validation
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> TrackMapper.toDomain(entity));
            assertTrue(exception.getMessage().contains("title must not be blank"),
                    "Should fail with domain validation message for blank title");
        }
    }

    @Nested
    @DisplayName("Bidirectional Mapping Tests")
    class BidirectionalTests {

        @Test
        @DisplayName("Should maintain data integrity in round-trip conversion")
        void roundTrip_shouldMaintainDataIntegrity() {
            // Given - Original domain object (respecting business rules)
            Track originalDomain = Track.of(
                    ISRC.of("FRLA12400001"),
                    "Round Trip Track",
                    List.of("Artist A", "Artist B", "Artist C"),
                    List.of(
                            Source.of("SPOTIFY", "spot-123"),
                            Source.of("TIDAL", "tidal-456"),
                            Source.of("DEEZER", "deezer-789")
                    ),
                    TrackStatus.VERIFIED
            );

            // When - Round trip: Domain -> Entity -> Domain
            TrackEntity entity = TrackMapper.toDbo(originalDomain);
            Track reconstructedDomain = TrackMapper.toDomain(entity);

            // Then - Verify complete equality
            assertEquals(originalDomain.isrc(), reconstructedDomain.isrc(), "ISRC should be preserved");
            assertEquals(originalDomain.title(), reconstructedDomain.title(), "Title should be preserved");
            assertEquals(originalDomain.status(), reconstructedDomain.status(), "Status should be preserved");

            // Verify artist names
            assertEquals(originalDomain.artistNames().size(), reconstructedDomain.artistNames().size(),
                    "Artist count should be preserved");
            for (String artistName : originalDomain.artistNames()) {
                assertTrue(reconstructedDomain.artistNames().contains(artistName),
                        "Artist '" + artistName + "' should be preserved");
            }

            // Verify sources
            assertEquals(originalDomain.sources().size(), reconstructedDomain.sources().size(),
                    "Source count should be preserved");
            for (Source originalSource : originalDomain.sources()) {
                boolean foundMatch = reconstructedDomain.sources().stream()
                        .anyMatch(s -> s.sourceName().equals(originalSource.sourceName()) &&
                                s.sourceId().equals(originalSource.sourceId()));
                assertTrue(foundMatch, "Source " + originalSource + " should be preserved");
            }
        }

        @Test
        @DisplayName("Should handle round-trip with minimal valid data")
        void roundTrip_shouldHandleMinimalValidData() {
            // Given - Minimal but VALID domain object (respects business rules)
            Track originalDomain = Track.of(
                    ISRC.of("FRLA12400002"),
                    "Minimal Track",
                    List.of("Single Artist"), // Must have at least one artist
                    List.of(Source.of("MANUAL", "manual-001")), // Must have at least one source
                    TrackStatus.PROVISIONAL
            );

            // When - Round trip
            TrackEntity entity = TrackMapper.toDbo(originalDomain);
            Track reconstructedDomain = TrackMapper.toDomain(entity);

            // Then
            assertEquals(originalDomain.isrc(), reconstructedDomain.isrc());
            assertEquals(originalDomain.title(), reconstructedDomain.title());
            assertEquals(originalDomain.status(), reconstructedDomain.status());
            assertEquals(1, reconstructedDomain.artistNames().size());
            assertEquals(1, reconstructedDomain.sources().size());
            assertEquals("Single Artist", reconstructedDomain.artistNames().get(0));
            assertEquals("MANUAL", reconstructedDomain.sources().get(0).sourceName());
        }
    }

    @Nested
    @DisplayName("JSON Serialization Edge Cases")
    class JsonSerializationTests {

        @Test
        @DisplayName("Should handle special characters in source data")
        void shouldHandleSpecialCharactersInSources() {
            // Given
            Track domain = Track.of(
                    ISRC.of("FRLA12400001"),
                    "Special Chars Track",
                    List.of("Artiste éçà", "Artist & Co"),
                    List.of(
                            Source.of("SPOTIFY", "id-with-special-chars-éèà"),
                            Source.of("MANUAL", "id with spaces and \"quotes\"")
                    ),
                    TrackStatus.PROVISIONAL
            );

            // When
            TrackEntity entity = TrackMapper.toDbo(domain);
            Track reconstructed = TrackMapper.toDomain(entity);

            // Then
            assertEquals(2, reconstructed.sources().size());

            Source spotifySource = reconstructed.sources().stream()
                    .filter(s -> "SPOTIFY".equals(s.sourceName()))
                    .findFirst()
                    .orElseThrow();
            assertEquals("id-with-special-chars-éèà", spotifySource.sourceId());

            Source manualSource = reconstructed.sources().stream()
                    .filter(s -> "MANUAL".equals(s.sourceName()))
                    .findFirst()
                    .orElseThrow();
            assertEquals("id with spaces and \"quotes\"", manualSource.sourceId());
        }

        @Test
        @DisplayName("Should handle very long source IDs")
        void shouldHandleLongSourceIds() {
            // Given
            String longSourceId = "a".repeat(1000); // Very long ID
            Track domain = Track.of(
                    ISRC.of("FRLA12400001"),
                    "Long ID Track",
                    List.of("Artist"),
                    List.of(Source.of("SPOTIFY", longSourceId)),
                    TrackStatus.VERIFIED
            );

            // When
            TrackEntity entity = TrackMapper.toDbo(domain);
            Track reconstructed = TrackMapper.toDomain(entity);

            // Then
            assertEquals(1, reconstructed.sources().size());
            assertEquals(longSourceId, reconstructed.sources().get(0).sourceId());
        }
    }
}