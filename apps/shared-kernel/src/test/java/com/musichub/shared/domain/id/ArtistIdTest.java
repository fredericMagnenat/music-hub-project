package com.musichub.shared.domain.id;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ArtistId Tests")
class ArtistIdTest {

    @Nested
    @DisplayName("Construction")
    class Construction {

        @Test
        @DisplayName("Should create ArtistId with valid UUID")
        void shouldCreateArtistIdWithValidUuid() {
            // Given
            UUID uuid = UUID.randomUUID();
            
            // When
            ArtistId artistId = new ArtistId(uuid);
            
            // Then
            assertNotNull(artistId);
            assertEquals(uuid, artistId.value());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when UUID is null")
        void shouldThrowExceptionWhenUuidIsNull() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, 
                () -> new ArtistId(null)
            );
            
            assertEquals("ArtistId null", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Factory Method")
    class FactoryMethod {

        @Test
        @DisplayName("Should generate new ArtistId with random UUID")
        void shouldGenerateNewArtistIdWithRandomUuid() {
            // When
            ArtistId artistId = ArtistId.newId();
            
            // Then
            assertNotNull(artistId);
            assertNotNull(artistId.value());
        }

        @Test
        @DisplayName("Should generate different UUIDs for each call")
        void shouldGenerateDifferentUuidsForEachCall() {
            // When
            ArtistId artistId1 = ArtistId.newId();
            ArtistId artistId2 = ArtistId.newId();
            
            // Then
            assertNotEquals(artistId1.value(), artistId2.value());
            assertNotEquals(artistId1, artistId2);
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
            ArtistId artistId1 = new ArtistId(uuid);
            ArtistId artistId2 = new ArtistId(uuid);
            
            // Then
            assertEquals(artistId1, artistId2);
            assertEquals(artistId1.hashCode(), artistId2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when UUIDs are different")
        void shouldNotBeEqualWhenUuidsAreDifferent() {
            // Given
            ArtistId artistId1 = new ArtistId(UUID.randomUUID());
            ArtistId artistId2 = new ArtistId(UUID.randomUUID());
            
            // Then
            assertNotEquals(artistId1, artistId2);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            // Given
            ArtistId artistId = new ArtistId(UUID.randomUUID());
            
            // Then
            assertNotEquals(artistId, null);
        }

        @Test
        @DisplayName("Should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            // Given
            ArtistId artistId = new ArtistId(UUID.randomUUID());
            String differentType = "not an ArtistId";
            
            // Then
            assertNotEquals(artistId, differentType);
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
            ArtistId artistId = new ArtistId(uuid);
            
            // When
            String result = artistId.toString();
            
            // Then
            assertNotNull(result);
            assertTrue(result.contains("ArtistId"));
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
            ArtistId artistId = new ArtistId(expectedUuid);
            
            // When
            UUID actualUuid = artistId.value();
            
            // Then
            assertEquals(expectedUuid, actualUuid);
            assertSame(expectedUuid, actualUuid);
        }
    }
}