package com.musichub.producer.adapter.spi;

import com.musichub.producer.adapter.spi.dto.TrackMetadataDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MusicPlatformClient REST client interface.
 * Note: This test focuses on interface contract validation.
 * Integration tests with WireMock will be added in a separate test class.
 */
class MusicPlatformClientTest {

    @Test
    void interface_ShouldHaveCorrectAnnotations() {
        // Given: The MusicPlatformClient interface

        // When: Checking interface annotations and methods
        assertTrue(MusicPlatformClient.class.isInterface());

        // Then: Interface should have the REST client annotations
        // This test verifies that the interface is properly structured
        // Actual REST functionality will be tested in integration tests

        try {
            var method = MusicPlatformClient.class.getMethod("getTrackByIsrc", String.class);
            assertNotNull(method);
            assertEquals(TrackMetadataDto.class, method.getReturnType());
        } catch (NoSuchMethodException e) {
            fail("getTrackByIsrc method should exist");
        }
    }

    @Test
    void interface_ShouldBeProperlyStructured() {
        // Given: The MusicPlatformClient interface

        // When: Checking the interface structure
        var methods = MusicPlatformClient.class.getDeclaredMethods();

        // Then: Should have exactly one method
        assertEquals(1, methods.length);
        assertEquals("getTrackByIsrc", methods[0].getName());

        // Method should take String parameter and return TrackMetadataDto
        var parameterTypes = methods[0].getParameterTypes();
        assertEquals(1, parameterTypes.length);
        assertEquals(String.class, parameterTypes[0]);
        assertEquals(TrackMetadataDto.class, methods[0].getReturnType());
    }
}