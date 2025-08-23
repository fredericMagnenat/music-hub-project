package com.musichub.producer.domain.values;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SourceTest {

    @Test
    @DisplayName("Should normalize and create Source when inputs are valid")
    void shouldCreateValidSource() {
        Source src = Source.of(" spotify ", " track123 ");
        assertEquals("SPOTIFY", src.sourceName());
        assertEquals("track123", src.sourceId());
    }

    @Test
    @DisplayName("Should reject unsupported sourceName")
    void shouldRejectUnsupportedSourceName() {
        assertThrows(IllegalArgumentException.class, () -> Source.of("UNKNOWN", "track123"));
    }

    @Test
    @DisplayName("Should reject blank sourceId")
    void shouldRejectBlankSourceId() {
        assertThrows(IllegalArgumentException.class, () -> Source.of("TIDAL", "   "));
    }
}
