package com.musichub.shared.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for CorrelationIdGenerator utility class.
 */
@DisplayName("CorrelationIdGenerator")
class CorrelationIdGeneratorTest {

    @Test
    @DisplayName("Should generate service-specific correlation ID when incoming ID is null")
    void shouldGenerateServiceCorrelationIdWhenIncomingIdIsNull() {
        // When
        String result = CorrelationIdGenerator.buildServiceCorrelationId(null, "producer");

        // Then
        assertNotNull(result);
        assertTrue(result.startsWith("producer-"));
        assertTrue(result.contains("-"));
        // Should have format: producer-{timestamp}-{uuid}
        String[] parts = result.split("-");
        assertTrue(parts.length >= 3);
        assertEquals("producer", parts[0]);
    }

    @Test
    @DisplayName("Should generate service-specific correlation ID when incoming ID is empty")
    void shouldGenerateServiceCorrelationIdWhenIncomingIdIsEmpty() {
        // When
        String result = CorrelationIdGenerator.buildServiceCorrelationId("", "producer");

        // Then
        assertNotNull(result);
        assertTrue(result.startsWith("producer-"));
    }

    @Test
    @DisplayName("Should generate service-specific correlation ID when incoming ID is whitespace")
    void shouldGenerateServiceCorrelationIdWhenIncomingIdIsWhitespace() {
        // When
        String result = CorrelationIdGenerator.buildServiceCorrelationId("   ", "producer");

        // Then
        assertNotNull(result);
        assertTrue(result.startsWith("producer-"));
    }

    @Test
    @DisplayName("Should append service suffix when incoming ID is provided")
    void shouldAppendServiceSuffixWhenIncomingIdIsProvided() {
        // Given
        String incomingId = "request-123";

        // When
        String result = CorrelationIdGenerator.buildServiceCorrelationId(incomingId, "producer");

        // Then
        assertEquals("request-123-producer-service", result);
    }

    @Test
    @DisplayName("Should handle incoming ID with existing suffix")
    void shouldHandleIncomingIdWithExistingSuffix() {
        // Given
        String incomingId = "request-123-artist-service";

        // When
        String result = CorrelationIdGenerator.buildServiceCorrelationId(incomingId, "producer");

        // Then
        assertEquals("request-123-artist-service-producer-service", result);
    }

    @Test
    @DisplayName("Should generate unique correlation IDs")
    void shouldGenerateUniqueCorrelationIds() {
        // Given
        Set<String> generatedIds = new HashSet<>();
        int numberOfGenerations = 1000;

        // When
        for (int i = 0; i < numberOfGenerations; i++) {
            String id = CorrelationIdGenerator.generateServiceCorrelationId("producer");
            generatedIds.add(id);
        }

        // Then
        assertEquals(numberOfGenerations, generatedIds.size(),
                "All generated correlation IDs should be unique");
    }

    @Test
    @DisplayName("Should generate correlation ID with correct format")
    void shouldGenerateCorrelationIdWithCorrectFormat() {
        // When
        String result = CorrelationIdGenerator.generateServiceCorrelationId("producer");

        // Then
        assertTrue(result.matches("producer-\\d+-[a-f0-9]+"),
                "Correlation ID should match expected format: producer-{timestamp}-{uuid}");

        // Verify timestamp is reasonable (within last minute)
        String[] parts = result.split("-");
        long timestamp = Long.parseLong(parts[1]);
        long now = System.currentTimeMillis();
        assertTrue(Math.abs(now - timestamp) < 60000,
                "Timestamp should be recent (within last minute)");
    }

    @Test
    @DisplayName("Should extract original correlation ID from service-specific one")
    void shouldExtractOriginalCorrelationIdFromServiceSpecificOne() {
        // Given
        String originalId = "request-123";
        String serviceId = "request-123-producer-service";

        // When
        String extracted = CorrelationIdGenerator.extractOriginalCorrelationId(serviceId, "producer");

        // Then
        assertEquals(originalId, extracted);
    }

    @Test
    @DisplayName("Should return same ID when no service suffix found")
    void shouldReturnSameIdWhenNoServiceSuffixFound() {
        // Given
        String randomId = "some-random-id";

        // When
        String extracted = CorrelationIdGenerator.extractOriginalCorrelationId(randomId, "producer");

        // Then
        assertEquals(randomId, extracted);
    }

    @Test
    @DisplayName("Should handle null and empty inputs gracefully")
    void shouldHandleNullAndEmptyInputsGracefully() {
        // When & Then
        assertNotNull(CorrelationIdGenerator.buildServiceCorrelationId(null, "producer"));
        assertNotNull(CorrelationIdGenerator.buildServiceCorrelationId("", "producer"));
        assertEquals("", CorrelationIdGenerator.extractOriginalCorrelationId("", "producer"));
        assertEquals(null, CorrelationIdGenerator.extractOriginalCorrelationId(null, "producer"));
    }

    @Test
    @DisplayName("Should work with different service names")
    void shouldWorkWithDifferentServiceNames() {
        // Given
        String incomingId = "request-456";

        // When
        String producerId = CorrelationIdGenerator.buildServiceCorrelationId(incomingId, "producer");
        String artistId = CorrelationIdGenerator.buildServiceCorrelationId(incomingId, "artist");
        String trackId = CorrelationIdGenerator.buildServiceCorrelationId(incomingId, "track");

        // Then
        assertEquals("request-456-producer-service", producerId);
        assertEquals("request-456-artist-service", artistId);
        assertEquals("request-456-track-service", trackId);
    }
}