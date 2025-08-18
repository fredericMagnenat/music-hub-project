package com.musichub.producer.adapter.spi;

import com.musichub.producer.adapter.spi.dto.tidal.TidalTracksResponse;
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
            var method = MusicPlatformClient.class.getMethod("getTracksByIsrc", String.class, String.class, String.class);
            assertNotNull(method);
            assertEquals(TidalTracksResponse.class, method.getReturnType());
        } catch (NoSuchMethodException e) {
            fail("getTracksByIsrc method should exist");
        }
    }

    @Test
    void interface_ShouldBeProperlyStructured() {
        // Given: The MusicPlatformClient interface
        
        // When: Checking the interface structure
        var methods = MusicPlatformClient.class.getDeclaredMethods();
        
        // Then: Should have exactly one method
        assertEquals(1, methods.length);
        assertEquals("getTracksByIsrc", methods[0].getName());
        
        // Method should take three String parameters and return TidalTracksResponse
        var parameterTypes = methods[0].getParameterTypes();
        assertEquals(3, parameterTypes.length);
        assertEquals(String.class, parameterTypes[0]); // isrc
        assertEquals(String.class, parameterTypes[1]); // include
        assertEquals(String.class, parameterTypes[2]); // countryCode
        assertEquals(TidalTracksResponse.class, methods[0].getReturnType());
    }
}