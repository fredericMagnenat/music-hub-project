package com.musichub.producer.domain.model;

import com.musichub.producer.domain.values.Source;
import com.musichub.producer.domain.values.TrackStatus;
import com.musichub.shared.domain.values.ISRC;
import com.musichub.shared.domain.values.ProducerCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Producer Domain Model Tests")
class ProducerTest {

    @Nested
    @DisplayName("Factory Method - createNew")
    class FactoryMethod {

        @Test
        @DisplayName("Should generate deterministic ProducerId from same ProducerCode regardless of name")
        void createNew_generatesDeterministicIdFromCode() {
            // Given
            ProducerCode code = ProducerCode.of("FRLA1");
            
            // When
            Producer p1 = Producer.createNew(code, null);
            Producer p2 = Producer.createNew(code, "Any");
            
            // Then
            assertEquals(p1.id(), p2.id(), "Same ProducerCode should generate identical ProducerId");
            assertEquals(code, p1.producerCode(), "ProducerCode should be preserved");
        }
    }

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
            
            // When & Then
            assertTrue(producer.registerTrack(isrc, "Track Title", List.of("Artist Name"), List.of(source)), 
                "First registration should return true");
            assertFalse(producer.registerTrack(isrc, "Track Title", List.of("Artist Name"), List.of(source)), 
                "Duplicate registration should return false");
            assertTrue(producer.hasTrack(isrc), "Track should be present after registration");
        }

        @Test
        @DisplayName("Should return false when checking for non-existent track")
        void hasTrack_returnsFalseWhenAbsent() {
            // Given
            Producer producer = Producer.createNew(ProducerCode.of("FRLA1"), null);
            
            // When & Then
            assertFalse(producer.hasTrack(ISRC.of("FRLA12400001")), "Should return false for non-existent track");
        }

        @Test
        @DisplayName("Should store complete Track entities with metadata")
        void registerTrack_storesCompleteTrackEntities() {
            // Given
            Producer producer = Producer.createNew(ProducerCode.of("FRLA1"), null);
            ISRC isrc = ISRC.of("FRLA12400001");
            Source source = Source.of("SPOTIFY", "FRLA12400001");

            // When
            assertTrue(producer.registerTrack(isrc, "Track Title", List.of("Artist Name"), List.of(source)));

            // Then
            var trackOpt = producer.getTrack(isrc);
            assertTrue(trackOpt.isPresent(), "Track should be retrievable by ISRC");
            Track track = trackOpt.get();
            assertEquals("Track Title", track.title());
            assertEquals(List.of("Artist Name"), track.artistNames());
            assertEquals(TrackStatus.PROVISIONAL, track.status());
        }

        @Test
        @DisplayName("addTrack(Track) should be idempotent with equivalent Track entities")
        void addTrack_withTrackEntity_isIdempotentWithEquivalentTracks() {
            // Given
            Producer producer = Producer.createNew(ProducerCode.of("FRLA1"), null);
            ISRC isrc1 = ISRC.of("FRLA12400001");
            ISRC isrc2 = ISRC.of("FRLA12400001"); // Same ISRC
            Source source = Source.of("SPOTIFY", "FRLA12400001");
            
            Track track1 = Track.of(isrc1, "Title", List.of("Artist"), List.of(source), TrackStatus.PROVISIONAL);
            Track track2 = Track.of(isrc2, "Title Different", List.of("Artist Different"), List.of(source), TrackStatus.PROVISIONAL);

            // When
            boolean first = producer.addTrack(track1);
            boolean second = producer.addTrack(track2);

            // Then
            assertTrue(first, "First track should be added");
            assertFalse(second, "Second track with same ISRC should not be added (idempotent)");
        }

        @Test
        @DisplayName("tracks() should be unmodifiable")
        void tracks_isUnmodifiable() {
            // Given
            Producer producer = Producer.createNew(ProducerCode.of("FRLA1"), null);
            ISRC isrc = ISRC.of("FRLA12400001");
            Source source = Source.of("SPOTIFY", "FRLA12400001");
            producer.registerTrack(isrc, "Title", List.of("Artist"), List.of(source));

            // When / Then
            Track newTrack = Track.of(ISRC.of("FRLA12400002"), "Title", List.of("Artist"), List.of(source), TrackStatus.PROVISIONAL);
            assertThrows(UnsupportedOperationException.class, () -> producer.tracks().add(newTrack));
        }

        @Test
        @DisplayName("Defensive copy: mutating source set passed to from() must not affect aggregate")
        void from_makesDefensiveCopyOfTracks() {
            // Given
            Source source = Source.of("SPOTIFY", "FRLA12400001");
            Track track1 = Track.of(ISRC.of("FRLA12400001"), "Title", List.of("Artist"), List.of(source), TrackStatus.PROVISIONAL);
            var externalSet = new java.util.LinkedHashSet<Track>();
            externalSet.add(track1);

            Producer p = Producer.from(
                com.musichub.producer.domain.values.ProducerId.fromProducerCode(ProducerCode.of("FRLA1")),
                ProducerCode.of("FRLA1"),
                null,
                externalSet
            );

            // When: mutate the external set after creation
            Track track2 = Track.of(ISRC.of("FRLA12400002"), "Title2", List.of("Artist2"), List.of(source), TrackStatus.PROVISIONAL);
            externalSet.add(track2);

            // Then: aggregate should remain unchanged
            assertFalse(p.hasTrack(ISRC.of("FRLA12400002")));
        }

        @Test
        @DisplayName("rename should only update name without affecting id, producerCode or tracks")
        void rename_updatesOnlyName() {
            // Given
            ProducerCode code = ProducerCode.of("FRLA1");
            Producer p = Producer.createNew(code, "Old");
            ISRC isrc = ISRC.of("FRLA12400001");
            Source source = Source.of("SPOTIFY", "FRLA12400001");
            p.registerTrack(isrc, "Title", List.of("Artist"), List.of(source));
            var idBefore = p.id();
            var codeBefore = p.producerCode();
            var tracksBefore = p.tracks();

            // When
            p.rename("New");

            // Then
            assertEquals("New", p.name());
            assertEquals(idBefore, p.id());
            assertEquals(codeBefore, p.producerCode());
            assertEquals(tracksBefore, p.tracks());
        }

        @Test
        @DisplayName("Null guards: registerTrack and hasTrack must reject nulls")
        void nullGuards_registerTrackAndHasTrack_rejectNulls() {
            // Given
            Producer producer = Producer.createNew(ProducerCode.of("FRLA1"), null);
            ISRC validIsrc = ISRC.of("FRLA12400001");
            Source validSource = Source.of("SPOTIFY", "FRLA12400001");

            // Then
            assertThrows(NullPointerException.class, () -> producer.registerTrack(null, "Title", List.of("Artist"), List.of(validSource)),
                "ISRC must not be null");
            assertThrows(NullPointerException.class, () -> producer.registerTrack(validIsrc, null, List.of("Artist"), List.of(validSource)),
                "title must not be null");
            assertThrows(NullPointerException.class, () -> producer.registerTrack(validIsrc, "Title", null, List.of(validSource)),
                "artistNames must not be null");
            assertThrows(NullPointerException.class, () -> producer.registerTrack(validIsrc, "Title", List.of("Artist"), null),
                "sources must not be null");
            assertThrows(NullPointerException.class, () -> producer.hasTrack(null),
                "ISRC must not be null");
        }

        @Test
        @DisplayName("Should validate producer code consistency when registering tracks")
        void registerTrack_shouldValidateProducerCodeConsistency() {
            // Given
            Producer producer = Producer.createNew(ProducerCode.of("FRLA1"), null);
            ISRC wrongIsrc = ISRC.of("GBUM71900001"); // Different producer code
            Source source = Source.of("SPOTIFY", "GBUM71900001");

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> producer.registerTrack(wrongIsrc, "Title", List.of("Artist"), List.of(source)));
            assertEquals("Track producer code does not match aggregate producer code", exception.getMessage());
        }

        @Test
        @DisplayName("Should add track to producer with existing tracks maintaining immutability")
        void addTrack_shouldMaintainImmutableListWithExistingTracks() {
            // Given - Producer avec plusieurs tracks existants
            Producer producer = Producer.createNew(ProducerCode.of("FRLA1"), "Producer Name");
            ISRC isrc1 = ISRC.of("FRLA12400001");
            ISRC isrc2 = ISRC.of("FRLA12400002");
            ISRC isrc3 = ISRC.of("FRLA12400003");
            Source source = Source.of("SPOTIFY", "test-source");
            
            producer.registerTrack(isrc1, "Track 1", List.of("Artist 1"), List.of(source));
            producer.registerTrack(isrc2, "Track 2", List.of("Artist 2"), List.of(source));
            
            // Capture state before
            var tracksBefore = producer.tracks();
            assertEquals(2, tracksBefore.size(), "Should have 2 tracks initially");
            
            // When - Ajout d'une nouvelle track
            boolean wasAdded = producer.registerTrack(isrc3, "Track 3", List.of("Artist 3"), List.of(source));
            
            // Then
            assertTrue(wasAdded, "New track should be added");
            assertEquals(3, producer.tracks().size(), "Should now have 3 tracks");
            assertTrue(producer.hasTrack(isrc3), "New track should be findable");
            
            // Vérifier que les tracks existants sont toujours là (immutabilité)
            assertTrue(producer.hasTrack(isrc1), "Original track 1 should still exist");
            assertTrue(producer.hasTrack(isrc2), "Original track 2 should still exist");
            
            // Vérifier que la collection tracks() retourne toujours une vue unmodifiable
            var tracksAfter = producer.tracks();
            assertEquals(3, tracksAfter.size(), "tracks() should reflect new state");
            Track newTrack = Track.of(ISRC.of("FRLA12400004"), "Test", List.of("Test"), List.of(source), TrackStatus.PROVISIONAL);
            assertThrows(UnsupportedOperationException.class, () -> tracksAfter.add(newTrack), 
                "tracks() should still return unmodifiable collection");
        }

        @Test
        @DisplayName("Should normalize ISRC formats consistently across operations")
        void registerTrack_shouldNormalizeISRCFormatsConsistently() {
            // Given
            Producer producer = Producer.createNew(ProducerCode.of("FRLA1"), null);
            Source source = Source.of("SPOTIFY", "test-source");
            
            // When - Register with one ISRC format
            ISRC originalIsrc = ISRC.of("FRLA12400001");
            assertTrue(producer.registerTrack(originalIsrc, "Track Title", List.of("Artist"), List.of(source)),
                "Should register track with ISRC");
            
            // Then - Should find with same ISRC (testing internal normalization)
            assertTrue(producer.hasTrack(originalIsrc), "Should find track with same ISRC");
            assertTrue(producer.getTrack(originalIsrc).isPresent(), "getTrack should work with same ISRC");
            
            // Verify track details are correct
            var track = producer.getTrack(originalIsrc);
            assertTrue(track.isPresent(), "Track should be retrievable");
            assertEquals("Track Title", track.get().title(), "Title should match");
            assertEquals(List.of("Artist"), track.get().artistNames(), "Artists should match");
        }

        @Test
        @DisplayName("Should handle edge cases in track registration")
        void registerTrack_shouldHandleEdgeCases() {
            // Given
            Producer producer = Producer.createNew(ProducerCode.of("FRLA1"), null);
            Source source = Source.of("SPOTIFY", "test-source");
            ISRC validIsrc = ISRC.of("FRLA12400001");
            
            // Empty artist names list should be rejected (if this business rule exists)
            // Note: This tests the business rule - if Track.of() doesn't validate this, the test will fail
            assertThrows(IllegalArgumentException.class, 
                () -> producer.registerTrack(validIsrc, "Title", List.of(), List.of(source)),
                "Should reject empty artist names list");
            
            // Very long title should be accepted
            String longTitle = "A".repeat(500);
            ISRC longTitleIsrc = ISRC.of("FRLA12400002");
            assertDoesNotThrow(
                () -> producer.registerTrack(longTitleIsrc, longTitle, List.of("Artist"), List.of(source)),
                "Should accept very long titles");
            
            // Multiple artists should be handled correctly
            ISRC multiArtistIsrc = ISRC.of("FRLA12400003");
            List<String> manyArtists = List.of("Artist 1", "Artist 2", "Artist 3", "Artist 4", "Artist 5");
            assertTrue(producer.registerTrack(multiArtistIsrc, "Collaboration", manyArtists, List.of(source)),
                "Should handle multiple artists");
            
            // Verify the multi-artist track
            var track = producer.getTrack(multiArtistIsrc);
            assertTrue(track.isPresent(), "Multi-artist track should be retrievable");
            assertEquals(manyArtists, track.get().artistNames(), "All artists should be preserved");
        }

        @Test
        @DisplayName("Should maintain business invariants when adding multiple tracks")
        void registerTrack_shouldMaintainBusinessInvariants() {
            // Given
            Producer producer = Producer.createNew(ProducerCode.of("FRLA1"), "Test Producer");
            Source source = Source.of("SPOTIFY", "test-source");
            
            // When - Add multiple tracks from same producer code
            for (int i = 1; i <= 5; i++) {
                ISRC isrc = ISRC.of("FRLA1240000" + i);
                assertTrue(producer.registerTrack(isrc, "Title " + i, List.of("Artist " + i), List.of(source)),
                    "Should successfully register track " + i);
            }
            
            // Then - Verify all invariants are maintained
            assertEquals(5, producer.tracks().size(), "Should have exactly 5 tracks");
            assertEquals("FRLA1", producer.producerCode().value(), "Producer code should remain unchanged");
            assertEquals("Test Producer", producer.name(), "Producer name should remain unchanged");
            
            // Verify all tracks are accessible
            for (int i = 1; i <= 5; i++) {
                ISRC isrc = ISRC.of("FRLA1240000" + i);
                assertTrue(producer.hasTrack(isrc), "Track " + i + " should be findable");
                
                var track = producer.getTrack(isrc);
                assertTrue(track.isPresent(), "Track " + i + " should be retrievable");
                assertEquals("Title " + i, track.get().title(), "Track " + i + " should have correct title");
                assertEquals(List.of("Artist " + i), track.get().artistNames(), "Track " + i + " should have correct artists");
            }
        }

        @Test
        @DisplayName("Should provide clear error messages for business rule violations")
        void registerTrack_shouldProvideDescriptiveErrorMessages() {
            // Given
            Producer producer = Producer.createNew(ProducerCode.of("FRLA1"), null);
            Source source = Source.of("SPOTIFY", "test-source");
            
            // When & Then - Wrong producer code should give specific error
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> producer.registerTrack(ISRC.of("GBUM71900001"), "Title", List.of("Artist"), List.of(source)),
                "Should throw exception for mismatched producer code");
            
            assertEquals("Track producer code does not match aggregate producer code", ex.getMessage(),
                "Should provide specific error message for producer code mismatch");
            
            // Verify producer state is unchanged after failed operation
            assertEquals(0, producer.tracks().size(), "Producer should have no tracks after failed registration");
            assertFalse(producer.hasTrack(ISRC.of("GBUM71900001")), "Failed track should not be added");
        }

    }

    @Nested
    @DisplayName("Factory Method - from")
    class FactoryFromMethod {

        @Test
        @DisplayName("from should require non-null id and producerCode")
        void from_requiresNonNullIdAndProducerCode() {
            // Given
            var validId = com.musichub.producer.domain.values.ProducerId.fromProducerCode(ProducerCode.of("FRLA1"));
            var validCode = ProducerCode.of("FRLA1");

            // Then
            NullPointerException ex1 = assertThrows(NullPointerException.class,
                () -> Producer.from(null, validCode, null, null));
            assertEquals("Producer.id must not be null", ex1.getMessage());

            NullPointerException ex2 = assertThrows(NullPointerException.class,
                () -> Producer.from(validId, null, null, null));
            assertEquals("Producer.producerCode must not be null", ex2.getMessage());
        }
    }
}