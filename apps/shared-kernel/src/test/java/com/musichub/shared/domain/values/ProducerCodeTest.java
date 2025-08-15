package com.musichub.shared.domain.values;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class ProducerCodeTest {

    @Test
    @DisplayName("Should create valid producer code with five characters")
    void validProducerCode_withFiveCharacters_shouldCreateSuccessfully() {
        // Given
        String code = "FRLA1";

        // When
        ProducerCode producerCode = ProducerCode.of(code);

        // Then
        assertEquals(code, producerCode.value());
    }

    @Test
    @DisplayName("Should extract first five characters from twelve character producer code")
    void validProducerCode_withTwelveCharacters_shouldCreateAndExtractFirstFiveChars() {
        // Given
        String twelveChars = "FRLA11234567"; // first five are valid registrant code

        // When
        ProducerCode producerCode = ProducerCode.of(twelveChars);

        // Then
        assertEquals("FRLA1", producerCode.value());
    }

    @ParameterizedTest
    @DisplayName("Should reject codes with invalid length")
    @ValueSource(strings = {"A", "AB", "ABC", "ABCD", "ABCDEF"})
    void invalidLength_shouldThrowException(String invalidCode) {
        // When / Then
        assertThrows(IllegalArgumentException.class, () -> ProducerCode.of(invalidCode));
    }

    @ParameterizedTest
    @DisplayName("Should reject codes with invalid format")
    @ValueSource(strings = {"12345", "ab123", "A1B2C", "AB12#", "AB 23"})
    void invalidFormat_shouldThrowException(String invalidCode) {
        // When / Then
        assertThrows(IllegalArgumentException.class, () -> ProducerCode.of(invalidCode));
    }

    @ParameterizedTest
    @DisplayName("Should reject null or empty codes")
    @NullAndEmptySource
    void nullOrEmptyCode_shouldThrowException(String invalidCode) {
        if (invalidCode == null) {
            NullPointerException exception = assertThrows(NullPointerException.class, () -> ProducerCode.of(null));
            assertEquals("ProducerCode must not be null", exception.getMessage());
        } else {
            assertThrows(IllegalArgumentException.class, () -> ProducerCode.of(invalidCode));
        }
    }

    @Test
    @DisplayName("Should extract producer code from ISRC")
    void createWithISRC_shouldExtractProducerCode() {
        // Given
        ISRC isrc = ISRC.of("USRC17607839");

        // When
        ProducerCode producerCode = ProducerCode.with(isrc);

        // Then
        assertEquals("USRC1", producerCode.value());
    }

    @Test
    @DisplayName("Should throw exception for null ISRC")
    void nullISRC_shouldThrowException() {
        // When / Then
        NullPointerException exception = assertThrows(NullPointerException.class, () -> ProducerCode.with(null));
        assertEquals("TrackIsrc must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should consider equal codes when twelve-char input shares same first five characters")
    void equalityOfExtractedCodes_whenTwelveCharsShareSamePrefix() {
        // Given
        ProducerCode fromFive = ProducerCode.of("FRLA1");
        ProducerCode fromTwelve = ProducerCode.of("FRLA11234567");

        // Then
        assertEquals(fromFive, fromTwelve);
        assertEquals(fromFive.value(), fromTwelve.value());
    }
}