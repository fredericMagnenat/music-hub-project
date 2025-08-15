package com.musichub.shared.domain.values;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ISRCTest {

    @Test
    @DisplayName("Should create valid ISRC with correct format")
    void shouldCreateValidISRCWithCorrectFormat() {
        String validISRC = "USRC17607839";
        ISRC isrc = ISRC.of(validISRC);
        assertEquals(validISRC, isrc.value());
    }

    @Test
    @DisplayName("Should create valid ISRC with dashes and remove them")
    void shouldCreateValidISRCWithDashesAndRemoveThem() {
        String isrcWithDashes = "US-RC1-76-07839";
        ISRC isrc = ISRC.of(isrcWithDashes);
        assertEquals(isrcWithDashes, isrc.value()); // La valeur originale avec tirets est conservée
    }

    @Test
    @DisplayName("Should create valid ISRC with multiple dashes")
    void shouldCreateValidISRCWithMultipleDashes() {
        String isrcWithMultipleDashes = "US-R-C-1-7-6-0-7-8-3-9";
        ISRC isrc = ISRC.of(isrcWithMultipleDashes);
        assertEquals(isrcWithMultipleDashes, isrc.value());
    }

    @Test
    @DisplayName("Should create valid ISRC with mixed alphanumeric characters")
    void shouldCreateValidISRCWithMixedAlphanumericCharacters() {
        String validISRC = "GBUM71505078";
        ISRC isrc = ISRC.of(validISRC);
        assertEquals(validISRC, isrc.value());
    }

    @Test
    @DisplayName("Should throw exception for null value")
    void shouldThrowExceptionForNullValue() {
        assertThrows(NullPointerException.class, () -> ISRC.of(null));
    }

    @Test
    @DisplayName("Should throw exception for invalid format - too short")
    void shouldThrowExceptionForInvalidFormatTooShort() {
        String shortISRC = "US123";
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> ISRC.of(shortISRC)
        );
        assertTrue(exception.getMessage().contains("ISRC value 'US123' is invalid"));
    }

    @Test
    @DisplayName("Should throw exception for invalid format - too long")
    void shouldThrowExceptionForInvalidFormatTooLong() {
        String longISRC = "USRC176078391234";
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> ISRC.of(longISRC)
        );
        assertTrue(exception.getMessage().contains("is invalid"));
    }

    @Test
    @DisplayName("Should throw exception for invalid characters")
    void shouldThrowExceptionForInvalidCharacters() {
        String invalidISRC = "US@C17607839";
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> ISRC.of(invalidISRC)
        );
        assertTrue(exception.getMessage().contains("is invalid"));
    }

    @Test
    @DisplayName("Should throw exception for invalid format with dashes but wrong length")
    void shouldThrowExceptionForInvalidFormatWithDashesButWrongLength() {
        String invalidISRC = "US-RC1-76";
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> ISRC.of(invalidISRC)
        );
        assertTrue(exception.getMessage().contains("is invalid"));
    }

    @Test
    @DisplayName("Should accept valid ISRC with dashes in different positions")
    void shouldAcceptValidISRCWithDashesInDifferentPositions() {
        assertAll("Valid ISRC formats with dashes",
            () -> assertDoesNotThrow(() -> ISRC.of("US-RC1-76-07839")),
            () -> assertDoesNotThrow(() -> ISRC.of("USRC1-760-7839")),
            () -> assertDoesNotThrow(() -> ISRC.of("US-R-C-1-7-6-0-7-8-3-9"))
        );
    }

    @Test
    @DisplayName("Should throw exception for lowercase letters (invalid format)")
    void shouldThrowExceptionForLowercaseLetters() {
        String invalidLowercase = "usrc17607839";
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ISRC.of(invalidLowercase)
        );
        assertTrue(exception.getMessage().contains("is invalid"));
    }

    @Test
    @DisplayName("Should throw exception for spaces in ISRC")
    void shouldThrowExceptionForSpacesInISRC() {
        String invalidWithSpaces = "US RC1 76 07839";
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ISRC.of(invalidWithSpaces)
        );
        assertTrue(exception.getMessage().contains("is invalid"));
    }

    @Test
    @DisplayName("Equality is based on original value (dash vs no dash are not equal)")
    void equalityBasedOnOriginalValue_dashVsNoDash_notEqual() {
        ISRC withDashes = ISRC.of("US-RC1-76-07839");
        ISRC withoutDashes = ISRC.of("USRC17607839");
        assertNotEquals(withDashes, withoutDashes);
        assertNotEquals(withDashes.hashCode(), withoutDashes.hashCode());
    }
}