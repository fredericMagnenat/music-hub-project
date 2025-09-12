package com.musichub.shared.domain.values;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SourceType Enum Tests")
class SourceTypeTest {

    @Test
    @DisplayName("Should have all required enum constants")
    void shouldHaveAllRequiredConstants() {
        // Given
        SourceType[] values = SourceType.values();

        // Then
        assertEquals(5, values.length);
        assertTrue(containsConstant(values, "SPOTIFY"));
        assertTrue(containsConstant(values, "TIDAL"));
        assertTrue(containsConstant(values, "DEEZER"));
        assertTrue(containsConstant(values, "APPLE_MUSIC"));
        assertTrue(containsConstant(values, "MANUAL"));
    }

    @Test
    @DisplayName("Should return correct value for each constant")
    void shouldReturnCorrectValue() {
        assertEquals("SPOTIFY", SourceType.SPOTIFY.getValue());
        assertEquals("TIDAL", SourceType.TIDAL.getValue());
        assertEquals("DEEZER", SourceType.DEEZER.getValue());
        assertEquals("APPLE_MUSIC", SourceType.APPLE_MUSIC.getValue());
        assertEquals("MANUAL", SourceType.MANUAL.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"SPOTIFY", "TIDAL", "DEEZER", "APPLE_MUSIC", "MANUAL"})
    @DisplayName("Should convert valid string to SourceType")
    void shouldConvertValidStringToSourceType(String value) {
        // When
        SourceType result = SourceType.fromString(value);

        // Then
        assertNotNull(result);
        assertEquals(value, result.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"spotify", "tidal", "deezer", "apple_music", "manual"})
    @DisplayName("Should convert lowercase string to SourceType")
    void shouldConvertLowercaseStringToSourceType(String value) {
        // When
        SourceType result = SourceType.fromString(value);

        // Then
        assertNotNull(result);
        assertEquals(value.toUpperCase(), result.getValue());
    }

    @Test
    @DisplayName("Should throw exception for invalid string")
    void shouldThrowExceptionForInvalidString() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> SourceType.fromString("INVALID")
        );
        assertTrue(exception.getMessage().contains("Unsupported source type"));
    }

    @Test
    @DisplayName("Should throw exception for null string")
    void shouldThrowExceptionForNullString() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> SourceType.fromString(null)
        );
        assertTrue(exception.getMessage().contains("cannot be null or empty"));
    }

    @Test
    @DisplayName("Should throw exception for empty string")
    void shouldThrowExceptionForEmptyString() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> SourceType.fromString("")
        );
        assertTrue(exception.getMessage().contains("cannot be null or empty"));
    }

    @Test
    @DisplayName("Should throw exception for blank string")
    void shouldThrowExceptionForBlankString() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> SourceType.fromString("   ")
        );
        assertTrue(exception.getMessage().contains("cannot be null or empty"));
    }

    private boolean containsConstant(SourceType[] values, String name) {
        for (SourceType value : values) {
            if (value.name().equals(name)) {
                return true;
            }
        }
        return false;
    }
}