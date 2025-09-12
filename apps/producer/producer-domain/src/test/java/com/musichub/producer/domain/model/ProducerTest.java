package com.musichub.producer.domain.model;

import com.musichub.producer.domain.values.ArtistCredit;
import com.musichub.shared.domain.values.Source;
import com.musichub.producer.domain.values.TrackStatus;
import com.musichub.shared.domain.id.ArtistId;
import com.musichub.shared.domain.values.ISRC;
import com.musichub.shared.domain.values.ProducerCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Producer Domain Model Tests")
class ProducerTest {

    @Nested
    @DisplayName("Track Management")
    class TrackManagement {

        @Test
        @DisplayName("Should register tracks with complete metadata and idempotent behavior")
        void registerTrack_isIdempotent_withCompleteMetadata() {
            // Given
            Producer producer = Producer.createNew(ProducerCode.of("FRLA1"), null);
            ISRC isrc = ISRC.of("FRLA12400001");
            Source source = Source.of("SPOTIFY", "FRLA12400001");
            List<ArtistCredit> credits = List.of(ArtistCredit.withName("Artist Name"));

            // When & Then
            assertTrue(producer.registerTrack(isrc, "Track Title", credits, List.of(source)),
                "First registration should return true");
            assertFalse(producer.registerTrack(isrc, "Track Title", credits, List.of(source)),
                "Duplicate registration should return false");
            assertTrue(producer.hasTrack(isrc), "Track should be present after registration");
        }

        @Test
        @DisplayName("Should store complete Track entities with metadata")
        void registerTrack_storesCompleteTrackEntities() {
            // Given
            Producer producer = Producer.createNew(ProducerCode.of("FRLA1"), null);
            ISRC isrc = ISRC.of("FRLA12400001");
            Source source = Source.of("SPOTIFY", "FRLA12400001");
            UUID artistId = UUID.randomUUID();
            List<ArtistCredit> credits = List.of(ArtistCredit.with("Artist Name", new ArtistId(artistId)));

            // When
            assertTrue(producer.registerTrack(isrc, "Track Title", credits, List.of(source)));

            // Then
            var trackOpt = producer.getTrack(isrc);
            assertTrue(trackOpt.isPresent(), "Track should be retrievable by ISRC");
            Track track = trackOpt.get();
            assertEquals("Track Title", track.title());
            assertEquals(credits, track.credits());
            assertEquals(TrackStatus.PROVISIONAL, track.status());
        }
    }
}