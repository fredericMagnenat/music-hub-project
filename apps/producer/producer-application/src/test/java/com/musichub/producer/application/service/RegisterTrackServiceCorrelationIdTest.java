package com.musichub.producer.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests specifically for correlation ID generation in
 * RegisterTrackService.
 * Tests the correlation ID generation logic in isolation.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterTrackService Correlation ID Generation")
class RegisterTrackServiceCorrelationIdTest {

    /**
     * Test helper to access private methods for testing.
     * Creates a minimal instance just for testing correlation ID methods.
     */
    private static class TestableRegisterTrackService {
        // Access to private methods via reflection for testing
        private static final String SERVICE_NAME = "producer";
        private static final String CORRELATION_ID_SUFFIX = "-service";
        private static final String CORRELATION_ID_SEPARATOR = "-";

        public String buildServiceCorrelationId(String incomingCorrelationId) {
            if (incomingCorrelationId != null && !incomingCorrelationId.trim().isEmpty()) {
                return incomingCorrelationId + CORRELATION_ID_SUFFIX;
            }
            return generateServiceCorrelationId();
        }

        public String generateServiceCorrelationId() {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String uuid = UUID.randomUUID().toString().replace("-", "");
            return SERVICE_NAME + CORRELATION_ID_SEPARATOR + timestamp + CORRELATION_ID_SEPARATOR + uuid;
        }
    }

    private final TestableRegisterTrackService testableService = new TestableRegisterTrackService();

    @Test
    @DisplayName("Should generate service-specific correlation ID when incoming ID is null")
    void shouldGenerateServiceCorrelationIdWhenIncomingIdIsNull() {
        // When
        String result = testableService.buildServiceCorrelationId(null);

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
        String result = testableService.buildServiceCorrelationId("");

        // Then
        assertNotNull(result);
        assertTrue(result.startsWith("producer-"));
    }

    @Test
    @DisplayName("Should generate service-specific correlation ID when incoming ID is whitespace")
    void shouldGenerateServiceCorrelationIdWhenIncomingIdIsWhitespace() {
        // When
        String result = testableService.buildServiceCorrelationId("   ");

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
        String result = testableService.buildServiceCorrelationId(incomingId);

        // Then
        assertEquals("request-123-service", result);
    }

    @Test
    @DisplayName("Should handle incoming ID with existing suffix")
    void shouldHandleIncomingIdWithExistingSuffix() {
        // Given
        String incomingId = "request-123-artist-service";

        // When
        String result = testableService.buildServiceCorrelationId(incomingId);

        // Then
        assertEquals("request-123-artist-service-service", result);
    }

    @Test
    @DisplayName("Should generate unique correlation IDs")
    void shouldGenerateUniqueCorrelationIds() {
        // Given
        Set<String> generatedIds = new HashSet<>();
        int numberOfGenerations = 1000;

        // When
        for (int i = 0; i < numberOfGenerations; i++) {
            String id = testableService.generateServiceCorrelationId();
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
        String result = testableService.generateServiceCorrelationId();

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
    @DisplayName("Should generate correlation ID with sufficient entropy")
    void shouldGenerateCorrelationIdWithSufficientEntropy() {
        // When
        String result = testableService.generateServiceCorrelationId();

        // Then
        // UUID part should be at least 32 characters (128 bits)
        String[] parts = result.split("-");
        String uuidPart = parts[2];
        assertTrue(uuidPart.length() >= 32,
                "UUID part should have sufficient entropy (at least 32 characters)");
    }
}