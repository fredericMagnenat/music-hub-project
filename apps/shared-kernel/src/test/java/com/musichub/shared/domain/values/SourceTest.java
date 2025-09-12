package com.musichub.shared.domain.values;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Source Value Object Tests")
class SourceTest {

    @Test
    @DisplayName("Should create Source with valid parameters")
    void shouldCreateSourceWithValidParameters() {
        // Given
        SourceType sourceType = SourceType.SPOTIFY;
        String sourceId = "test-id-123";

        // When
        Source source = new Source(sourceType, sourceId);

        // Then
        assertEquals(sourceType, source.sourceType());
        assertEquals(sourceId, source.sourceId());
        assertEquals("SPOTIFY", source.getSourceName());
    }

    @Test
    @DisplayName("Should create Source using factory method with string")
    void shouldCreateSourceUsingFactoryMethodWithString() {
        // When
        Source source = Source.of("TIDAL", "external-id-456");

        // Then
        assertEquals(SourceType.TIDAL, source.sourceType());
        assertEquals("external-id-456", source.sourceId());
        assertEquals("TIDAL", source.getSourceName());
    }

    @Test
    @DisplayName("Should normalize sourceId by trimming")
    void shouldNormalizeSourceIdByTrimming() {
        // When
        Source source = new Source(SourceType.DEEZER, "  test-id  ");

        // Then
        assertEquals("test-id", source.sourceId());
    }

    @Test
    @DisplayName("Should throw exception for null SourceType")
    void shouldThrowExceptionForNullSourceType() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new Source(null, "test-id")
        );
        assertTrue(exception.getMessage().contains("sourceType must not be null"));
    }

    @Test
    @DisplayName("Should throw exception for null sourceId")
    void shouldThrowExceptionForNullSourceId() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new Source(SourceType.SPOTIFY, null)
        );
        assertTrue(exception.getMessage().contains("sourceId must not be null"));
    }

    @Test
    @DisplayName("Should throw exception for blank sourceId")
    void shouldThrowExceptionForBlankSourceId() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Source(SourceType.SPOTIFY, "")
        );
        assertTrue(exception.getMessage().contains("sourceId must not be blank"));
    }

    @Test
    @DisplayName("Should throw exception for whitespace-only sourceId")
    void shouldThrowExceptionForWhitespaceOnlySourceId() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Source(SourceType.SPOTIFY, "   ")
        );
        assertTrue(exception.getMessage().contains("sourceId must not be blank"));
    }

    @Test
    @DisplayName("Should throw exception in factory method for invalid source name")
    void shouldThrowExceptionInFactoryMethodForInvalidSourceName() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Source.of("INVALID_SOURCE", "test-id")
        );
        assertTrue(exception.getMessage().contains("Unsupported source type"));
    }

    @Test
    @DisplayName("Should handle case-insensitive source name in factory method")
    void shouldHandleCaseInsensitiveSourceNameInFactoryMethod() {
        // When
        Source source = Source.of("spotify", "test-id");

        // Then
        assertEquals(SourceType.SPOTIFY, source.sourceType());
    }

    @Test
    @DisplayName("Should be equal when sourceType and sourceId are the same")
    void shouldBeEqualWhenSourceTypeAndSourceIdAreTheSame() {
        Source source1 = new Source(SourceType.SPOTIFY, "test-id");
        Source source2 = new Source(SourceType.SPOTIFY, "test-id");

        assertEquals(source1, source2);
        assertEquals(source1.hashCode(), source2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when sourceType differs")
    void shouldNotBeEqualWhenSourceTypeDiffers() {
        Source source1 = new Source(SourceType.SPOTIFY, "test-id");
        Source source2 = new Source(SourceType.TIDAL, "test-id");

        assertNotEquals(source1, source2);
    }

    @Test
    @DisplayName("Should not be equal when sourceId differs")
    void shouldNotBeEqualWhenSourceIdDiffers() {
        Source source1 = new Source(SourceType.SPOTIFY, "test-id-1");
        Source source2 = new Source(SourceType.SPOTIFY, "test-id-2");

        assertNotEquals(source1, source2);
    }

    @Test
    @DisplayName("Should convert to string correctly")
    void shouldConvertToStringCorrectly() {
        Source source = new Source(SourceType.APPLE_MUSIC, "test-id");
        String expected = "Source[sourceType=APPLE_MUSIC, sourceId=test-id]";

        assertEquals(expected, source.toString());
    }
}