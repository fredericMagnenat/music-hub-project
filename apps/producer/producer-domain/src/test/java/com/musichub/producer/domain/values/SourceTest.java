package com.musichub.producer.domain.values;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SourceTest {

    @Test
    @DisplayName("Should normalize and create Source when inputs are valid")
    void shouldCreateValidSource() {
        Source src = Source.of(" spotify ", " v1 ");
        assertEquals("SPOTIFY", src.platform());
        assertEquals("v1", src.apiVersion());
    }

    @Test
    @DisplayName("Should reject unsupported platform")
    void shouldRejectUnsupportedPlatform() {
        assertThrows(IllegalArgumentException.class, () -> Source.of("UNKNOWN", "v1"));
    }

    @Test
    @DisplayName("Should reject blank apiVersion")
    void shouldRejectBlankApiVersion() {
        assertThrows(IllegalArgumentException.class, () -> Source.of("TIDAL", "   "));
    }
}
