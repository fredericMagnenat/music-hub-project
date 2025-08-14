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
    }
}
