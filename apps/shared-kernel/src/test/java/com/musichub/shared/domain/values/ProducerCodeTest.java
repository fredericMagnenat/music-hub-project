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
    void validProducerCode_withFiveCharacters_shouldCreateSuccessfully() {/* implementation omitted for shortness */}

    @Test
    @DisplayName("Should extract first five characters from twelve character producer code")
    void validProducerCode_withTwelveCharacters_shouldCreateAndExtractFirstFiveChars() {/* implementation omitted for shortness */}

    @ParameterizedTest
    @DisplayName("Should reject codes with invalid length")
    @ValueSource(strings = {"A", "AB", "ABC", "ABCD", "ABCDEF"})
    void invalidLength_shouldThrowException(String invalidCode) {/* implementation omitted for shortness */}

    @ParameterizedTest
    @DisplayName("Should reject codes with invalid format")
    @ValueSource(strings = {"12345", "ab123", "A1B2C", "AB12#", "AB 23"})
    void invalidFormat_shouldThrowException(String invalidCode) {/* implementation omitted for shortness */}

    @ParameterizedTest
    @DisplayName("Should reject null or empty codes")
    @NullAndEmptySource
    void nullOrEmptyCode_shouldThrowException(String invalidCode) {/* implementation omitted for shortness */}

    @Test
    @DisplayName("Should extract producer code from ISRC")
    void createWithISRC_shouldExtractProducerCode() {/* implementation omitted for shortness */}

    @Test
    @DisplayName("Should throw exception for null ISRC")
    void nullISRC_shouldThrowException() {/* implementation omitted for shortness */}
}