package com.musichub.producer.adapter.spi.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TrackNotFoundInExternalServiceException
 */
class TrackNotFoundInExternalServiceExceptionTest {

    @Test
    void constructor_WithMessageIsrcAndPlatform_ShouldSetAllFields() {
        // Given
        String message = "Track not found";
        String isrc = "GBUM71507409";
        String platform = "tidal";

        // When
        TrackNotFoundInExternalServiceException exception = 
            new TrackNotFoundInExternalServiceException(message, isrc, platform);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(isrc, exception.getIsrc());
        assertEquals(platform, exception.getPlatform());
        assertNull(exception.getCause());
    }

    @Test
    void constructor_WithMessageIsrcPlatformAndCause_ShouldSetAllFields() {
        // Given
        String message = "Track not found due to network error";
        String isrc = "GBUM71507409";
        String platform = "tidal";
        RuntimeException cause = new RuntimeException("Network timeout");

        // When
        TrackNotFoundInExternalServiceException exception = 
            new TrackNotFoundInExternalServiceException(message, isrc, platform, cause);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(isrc, exception.getIsrc());
        assertEquals(platform, exception.getPlatform());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void getIsrc_ShouldReturnCorrectValue() {
        // Given
        String expectedIsrc = "USRC17607839";
        TrackNotFoundInExternalServiceException exception = 
            new TrackNotFoundInExternalServiceException("Test message", expectedIsrc, "spotify");

        // When
        String actualIsrc = exception.getIsrc();

        // Then
        assertEquals(expectedIsrc, actualIsrc);
    }

    @Test
    void getPlatform_ShouldReturnCorrectValue() {
        // Given
        String expectedPlatform = "spotify";
        TrackNotFoundInExternalServiceException exception = 
            new TrackNotFoundInExternalServiceException("Test message", "USRC17607839", expectedPlatform);

        // When
        String actualPlatform = exception.getPlatform();

        // Then
        assertEquals(expectedPlatform, actualPlatform);
    }

    @Test
    void exception_ShouldBeRuntimeException() {
        // Given
        TrackNotFoundInExternalServiceException exception = 
            new TrackNotFoundInExternalServiceException("Test", "ISRC123", "platform");

        // Then
        assertTrue(exception instanceof RuntimeException);
    }
}