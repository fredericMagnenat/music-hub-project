package com.musichub.producer.domain.values;

import com.musichub.shared.domain.values.ProducerCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProducerId Tests")
class ProducerIdTest {

    @Nested
    @DisplayName("Factory Method - fromProducerCode")
    class FactoryMethod {

        @Test
        @DisplayName("Should generate identical UUIDs for identical producer codes")
        void sameProducerCodeYieldsSameUuid() {
            // Given
            ProducerCode code1 = ProducerCode.of("FRLA1");
            ProducerCode code2 = ProducerCode.of("FRLA1");

            // When
            ProducerId id1 = ProducerId.fromProducerCode(code1);
            ProducerId id2 = ProducerId.fromProducerCode(code2);

            // Then
            assertEquals(id1, id2);
            assertEquals(id1.value(), id2.value());
        }

        @Test
        @DisplayName("Should generate different UUIDs for different producer codes")
        void differentProducerCodesYieldDifferentUuids() {
            // Given
            ProducerCode code1 = ProducerCode.of("FRLA1");
            ProducerCode code2 = ProducerCode.of("FRLA2");

            // When
            ProducerId id1 = ProducerId.fromProducerCode(code1);
            ProducerId id2 = ProducerId.fromProducerCode(code2);

            // Then
            assertNotEquals(id1, id2);
        }

        @Test
        @DisplayName("Should throw NullPointerException when ProducerCode is null")
        void shouldThrowExceptionWhenProducerCodeIsNull() {
            // When & Then
            NullPointerException exception = assertThrows(
                NullPointerException.class, 
                () -> ProducerId.fromProducerCode(null)
            );
            
            assertEquals("ProducerCode must not be null", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Construction Validation")
    class ConstructionValidation {

        @Test
        @DisplayName("Should throw IllegalArgumentException when UUID value is null")
        void valueCannotBeNull() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, 
                () -> new ProducerId(null)
            );
            
            assertEquals("ProducerId value must not be null", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("UUID v5 Implementation")
    class UuidV5Implementation {

        @Test
        @DisplayName("Should generate deterministic UUID v5 matching known snapshot")
        void deterministicKnownValueSnapshot() {
            // Given
            ProducerCode code = ProducerCode.of("FRLA1");
            
            // When
            ProducerId id = ProducerId.fromProducerCode(code);
            
            // Then - Snapshot derived from current implementation and namespace constant
            UUID expected = UUID.fromString("f36e54fa-ce8b-5498-9713-c231236ef2e8");
            assertEquals(expected, id.value());
        }

        @Test
        @DisplayName("Should generate valid UUID v5 format")
        void shouldGenerateValidUuidV5Format() {
            // Given
            ProducerCode code = ProducerCode.of("FRLA1");
            
            // When
            ProducerId id = ProducerId.fromProducerCode(code);
            UUID uuid = id.value();
            
            // Then - Verify UUID v5 characteristics
            assertEquals(5, uuid.version(), "Should be UUID version 5");
            assertEquals(2, uuid.variant(), "Should use IETF variant");
        }

        @Test
        @DisplayName("Equality and hashCode should be consistent for same ProducerCode")
        void equalityAndHashCode_consistentForSameProducerCode() {
            // Given
            ProducerCode code = ProducerCode.of("FRLA1");

            // When
            ProducerId id1 = ProducerId.fromProducerCode(code);
            ProducerId id2 = ProducerId.fromProducerCode(code);

            // Then
            assertEquals(id1, id2);
            assertEquals(id1.hashCode(), id2.hashCode());
        }
    }
}
