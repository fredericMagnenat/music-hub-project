package com.musichub.producer.adapter.persistence.mapper;

import com.musichub.producer.adapter.persistence.entity.ArtistCreditEmbeddable;
import com.musichub.producer.adapter.persistence.entity.TrackEntity;
import com.musichub.producer.domain.model.Track;
import com.musichub.producer.domain.values.ArtistCredit;
import com.musichub.shared.domain.values.Source;
import com.musichub.producer.domain.values.TrackStatus;
import com.musichub.shared.domain.id.ArtistId;
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
            UUID artistId = UUID.randomUUID();
            List<ArtistCredit> credits = List.of(ArtistCredit.with("Artist 1", new ArtistId(artistId)), ArtistCredit.withName("Artist 2"));
            List<Source> sources = List.of(
                    Source.of("SPOTIFY", "spotify-123"),
                    Source.of("APPLE_MUSIC", "apple-456")
            );
            TrackStatus status = TrackStatus.PROVISIONAL;

            Track domain = Track.of(isrc, title, credits, sources, status);

            // When
            TrackEntity entity = TrackMapper.toDbo(domain);

            // Then
            assertNotNull(entity, "Entity should not be null");
            assertEquals("FRLA12400001", entity.getIsrc(), "ISRC should be mapped correctly");
            assertEquals("Test Track", entity.getTitle(), "Title should be mapped correctly");
            assertEquals("PROVISIONAL", entity.getStatus(), "Status should be mapped as enum name");

            assertEquals(2, entity.getCredits().size(), "Should have 2 artists");
            assertTrue(entity.getCredits().stream().anyMatch(c -> c.getArtistName().equals("Artist 1") && c.getArtistId().equals(artistId)), "Should contain first artist with ID");
            assertTrue(entity.getCredits().stream().anyMatch(c -> c.getArtistName().equals("Artist 2") && c.getArtistId() == null), "Should contain second artist without ID");

            assertNotNull(entity.getSources(), "Sources should not be null");
            assertEquals(2, entity.getSources().size(), "Should have 2 sources");
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
            UUID artistId = UUID.randomUUID();
            entity.setCredits(List.of(new ArtistCreditEmbeddable("Artist 1", artistId), new ArtistCreditEmbeddable("Artist 2", null)));
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

            assertEquals(2, domain.credits().size(), "Should have 2 artists");
            assertTrue(domain.credits().stream().anyMatch(c -> c.artistName().equals("Artist 1") && c.artistId().value().equals(artistId)), "Should contain first artist with ID");
            assertTrue(domain.credits().stream().anyMatch(c -> c.artistName().equals("Artist 2") && c.artistId() == null), "Should contain second artist without ID");
        }
    }

    @Nested
    @DisplayName("Bidirectional Mapping Tests")
    class BidirectionalTests {

        @Test
        @DisplayName("Should maintain data integrity in round-trip conversion")
        void roundTrip_shouldMaintainDataIntegrity() {
            // Given - Original domain object
            UUID artistId = UUID.randomUUID();
            Track originalDomain = Track.of(
                    ISRC.of("FRLA12400001"),
                    "Round Trip Track",
                    List.of(ArtistCredit.with("Artist A", new ArtistId(artistId)), ArtistCredit.withName("Artist B")),
                    List.of(Source.of("SPOTIFY", "spot-123")),
                    TrackStatus.VERIFIED
            );

            // When - Round trip: Domain -> Entity -> Domain
            TrackEntity entity = TrackMapper.toDbo(originalDomain);
            Track reconstructedDomain = TrackMapper.toDomain(entity);

            // Then - Verify complete equality
            assertEquals(originalDomain.isrc(), reconstructedDomain.isrc());
            assertEquals(originalDomain.title(), reconstructedDomain.title());
            assertEquals(originalDomain.status(), reconstructedDomain.status());
            assertEquals(originalDomain.credits(), reconstructedDomain.credits());
            assertEquals(originalDomain.sources(), reconstructedDomain.sources());
        }
    }
}