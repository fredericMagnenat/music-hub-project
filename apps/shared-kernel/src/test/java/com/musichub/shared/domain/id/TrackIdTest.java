package com.musichub.shared.domain.id;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TrackId Tests")
class TrackIdTest {

    @Nested
    @DisplayName("Construction")
    class Construction {

        @Test
        @DisplayName("Should create TrackId with valid UUID")
        void shouldCreateTrackIdWithValidUuid() {
            // Given
            UUID uuid = UUID.randomUUID();
            
            // When
            TrackId trackId = new TrackId(uuid);
            
            // Then
            assertNotNull(trackId);
            assertEquals(uuid, trackId.value());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when UUID is null")
        void shouldThrowExceptionWhenUuidIsNull() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, 
                () -> new TrackId(null)
            );
            
            assertEquals("TrackId null", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Factory Method")
    class FactoryMethod {

        @Test
        @DisplayName("Should generate new TrackId with random UUID")
        void shouldGenerateNewTrackIdWithRandomUuid() {
            // When
            TrackId trackId = TrackId.newId();
            
            // Then
            assertNotNull(trackId);
            assertNotNull(trackId.value());
        }

        @Test
        @DisplayName("Should generate different UUIDs for each call")
        void shouldGenerateDifferentUuidsForEachCall() {
            // When
            TrackId trackId1 = TrackId.newId();
            TrackId trackId2 = TrackId.newId();
            
            // Then
            assertNotEquals(trackId1.value(), trackId2.value());
            assertNotEquals(trackId1, trackId2);
        }
    }

    @Nested
    @DisplayName("Deterministic Factory Method")
    class DeterministicFactoryMethod {

        @Test
        @DisplayName("Should generate same TrackId for same ISRC")
        void shouldGenerateSameTrackIdForSameISRC() {
            // Given
            String isrc = "USRC17607839";
            
            // When
            TrackId trackId1 = TrackId.fromISRC(isrc);
            TrackId trackId2 = TrackId.fromISRC(isrc);
            
            // Then
            assertEquals(trackId1, trackId2);
            assertEquals(trackId1.value(), trackId2.value());
        }

        @Test
        @DisplayName("Should generate different TrackIds for different ISRCs")
        void shouldGenerateDifferentTrackIdsForDifferentISRCs() {
            // When
            TrackId trackId1 = TrackId.fromISRC("USRC17607839");
            TrackId trackId2 = TrackId.fromISRC("GBUM71505078");
            
            // Then
            assertNotEquals(trackId1, trackId2);
            assertNotEquals(trackId1.value(), trackId2.value());
        }

        @Test
        @DisplayName("Should handle whitespace in ISRC")
        void shouldHandleWhitespaceInISRC() {
            // Given
            String isrc = " USRC17607839 ";
            
            // When
            TrackId trackId1 = TrackId.fromISRC(isrc);
            TrackId trackId2 = TrackId.fromISRC("USRC17607839");
            
            // Then
            assertEquals(trackId1, trackId2);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when ISRC is null")
        void shouldThrowExceptionWhenISRCIsNull() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, 
                () -> TrackId.fromISRC(null)
            );
            
            assertEquals("ISRC must not be null", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when ISRC is empty")
        void shouldThrowExceptionWhenISRCIsEmpty() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, 
                () -> TrackId.fromISRC("")
            );
            
            assertEquals("ISRC must not be empty", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when ISRC is blank")
        void shouldThrowExceptionWhenISRCIsBlank() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, 
                () -> TrackId.fromISRC("   ")
            );
            
            assertEquals("ISRC must not be empty", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Equality and Hash Code")
    class EqualityAndHashCode {

        @Test
        @DisplayName("Should be equal when UUIDs are the same")
        void shouldBeEqualWhenUuidsAreSame() {
            // Given
            UUID uuid = UUID.randomUUID();
            TrackId trackId1 = new TrackId(uuid);
            TrackId trackId2 = new TrackId(uuid);
            
            // Then
            assertEquals(trackId1, trackId2);
            assertEquals(trackId1.hashCode(), trackId2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when UUIDs are different")
        void shouldNotBeEqualWhenUuidsAreDifferent() {
            // Given
            TrackId trackId1 = new TrackId(UUID.randomUUID());
            TrackId trackId2 = new TrackId(UUID.randomUUID());
            
            // Then
            assertNotEquals(trackId1, trackId2);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            // Given
            TrackId trackId = new TrackId(UUID.randomUUID());
            
            // Then
            assertNotEquals(trackId, null);
        }

        @Test
        @DisplayName("Should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            // Given
            TrackId trackId = new TrackId(UUID.randomUUID());
            String differentType = "not a TrackId";
            
            // Then
            assertNotEquals(trackId, differentType);
        }
    }

    @Nested
    @DisplayName("toString Method")
    class ToStringMethod {

        @Test
        @DisplayName("Should return string representation with UUID value")
        void shouldReturnStringRepresentationWithUuidValue() {
            // Given
            UUID uuid = UUID.randomUUID();
            TrackId trackId = new TrackId(uuid);
            
            // When
            String result = trackId.toString();
            
            // Then
            assertNotNull(result);
            assertTrue(result.contains("TrackId"));
            assertTrue(result.contains(uuid.toString()));
        }
    }

    @Nested
    @DisplayName("Value Access")
    class ValueAccess {

        @Test
        @DisplayName("Should return the same UUID value that was provided")
        void shouldReturnSameUuidValueThatWasProvided() {
            // Given
            UUID expectedUuid = UUID.randomUUID();
            TrackId trackId = new TrackId(expectedUuid);
            
            // When
            UUID actualUuid = trackId.value();
            
            // Then
            assertEquals(expectedUuid, actualUuid);
            assertSame(expectedUuid, actualUuid);
        }
    }
}