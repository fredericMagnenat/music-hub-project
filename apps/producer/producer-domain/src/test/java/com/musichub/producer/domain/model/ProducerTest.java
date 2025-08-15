package com.musichub.producer.domain.model;

import com.musichub.shared.domain.values.ISRC;
import com.musichub.shared.domain.values.ProducerCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
        @DisplayName("Should add tracks with idempotent behavior and normalize ISRC input")
        void addTrack_isIdempotent_andNormalizesInput() {
            // Given
            Producer producer = Producer.createNew(ProducerCode.of("FRLA1"), null);
            
            // When & Then
            assertTrue(producer.addTrack("fr-la1-24-00001"), "First add should return true");
            assertFalse(producer.addTrack("FRLA12400001"), "Duplicate add should return false after normalization");
            assertTrue(producer.hasTrack(ISRC.of("FRLA12400001")), "Track should be present regardless of input format");
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
        @DisplayName("Should normalize and store canonical ISRC representation in tracks set")
        void addTrack_normalizesAndStoresCanonicalValue() {
            // Given
            Producer producer = Producer.createNew(ProducerCode.of("FRLA1"), null);

            // When
            assertTrue(producer.addTrack("fr-la1-24-00001"));

            // Then
            assertTrue(producer.tracks().contains(ISRC.of("FRLA12400001")));
        }

        @Test
        @DisplayName("addTrack(ISRC) should be idempotent with differently formatted but equivalent values")
        void addTrack_withISRC_isIdempotentWithEquivalentFormats() {
            // Given
            Producer producer = Producer.createNew(ProducerCode.of("FRLA1"), null);

            // When
            boolean first = producer.addTrack(ISRC.of("FR-LA1-24-00001"));
            boolean second = producer.addTrack(ISRC.of("FRLA12400001"));

            // Then
            assertTrue(first);
            assertFalse(second);
        }

        @Test
        @DisplayName("tracks() should be unmodifiable")
        void tracks_isUnmodifiable() {
            // Given
            Producer producer = Producer.createNew(ProducerCode.of("FRLA1"), null);
            producer.addTrack("FRLA12400001");

            // When / Then
            assertThrows(UnsupportedOperationException.class, () -> producer.tracks().add(ISRC.of("FRLA12400002")));
        }

        @Test
        @DisplayName("Defensive copy: mutating source set passed to from() must not affect aggregate")
        void from_makesDefensiveCopyOfTracks() {
            // Given
            var externalSet = new java.util.LinkedHashSet<ISRC>();
            externalSet.add(ISRC.of("FRLA12400001"));

            Producer p = Producer.from(
                com.musichub.producer.domain.values.ProducerId.fromProducerCode(ProducerCode.of("FRLA1")),
                ProducerCode.of("FRLA1"),
                null,
                externalSet
            );

            // When: mutate the external set after creation
            externalSet.add(ISRC.of("FRLA12400002"));

            // Then: aggregate should remain unchanged
            assertFalse(p.hasTrack(ISRC.of("FRLA12400002")));
        }

        @Test
        @DisplayName("rename should only update name without affecting id, producerCode or tracks")
        void rename_updatesOnlyName() {
            // Given
            ProducerCode code = ProducerCode.of("FRLA1");
            Producer p = Producer.createNew(code, "Old");
            p.addTrack("FRLA12400001");
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
        @DisplayName("Null guards: addTrack(String), addTrack(ISRC), hasTrack(ISRC) must reject nulls")
        void nullGuards_addAndHasTrack_rejectNulls() {
            // Given
            Producer producer = Producer.createNew(ProducerCode.of("FRLA1"), null);

            // Then
            assertThrows(NullPointerException.class, () -> producer.addTrack((String) null),
                "isrcValue must not be null");
            assertThrows(NullPointerException.class, () -> producer.addTrack((ISRC) null),
                "ISRC must not be null");
            assertThrows(NullPointerException.class, () -> producer.hasTrack(null),
                "ISRC must not be null");
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
