package com.musichub.producer.domain.values;

import com.musichub.shared.domain.values.Source;
import com.musichub.shared.domain.values.SourceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProducerSourcePriority Tests")
class ProducerSourcePriorityTest {

    @Test
    @DisplayName("Should have correct priority values")
    void shouldHaveCorrectPriorityValues() {
        assertEquals(1, ProducerSourcePriority.MANUAL.getPriorityValue());
        assertEquals(2, ProducerSourcePriority.TIDAL.getPriorityValue());
        assertEquals(3, ProducerSourcePriority.SPOTIFY.getPriorityValue());
        assertEquals(4, ProducerSourcePriority.DEEZER.getPriorityValue());
        assertEquals(5, ProducerSourcePriority.APPLE_MUSIC.getPriorityValue());
    }

    @Test
    @DisplayName("Should convert SourceType to ProducerSourcePriority")
    void shouldConvertSourceTypeToProducerSourcePriority() {
        assertEquals(ProducerSourcePriority.MANUAL, ProducerSourcePriority.fromSourceType(SourceType.MANUAL));
        assertEquals(ProducerSourcePriority.TIDAL, ProducerSourcePriority.fromSourceType(SourceType.TIDAL));
        assertEquals(ProducerSourcePriority.SPOTIFY, ProducerSourcePriority.fromSourceType(SourceType.SPOTIFY));
        assertEquals(ProducerSourcePriority.DEEZER, ProducerSourcePriority.fromSourceType(SourceType.DEEZER));
        assertEquals(ProducerSourcePriority.APPLE_MUSIC, ProducerSourcePriority.fromSourceType(SourceType.APPLE_MUSIC));
    }

    @Test
    @DisplayName("Should convert Source to ProducerSourcePriority")
    void shouldConvertSourceToProducerSourcePriority() {
        // Given
        Source manualSource = Source.of("MANUAL", "test-id");
        Source tidalSource = Source.of("TIDAL", "test-id");

        // When & Then
        assertEquals(ProducerSourcePriority.MANUAL, ProducerSourcePriority.fromSource(manualSource));
        assertEquals(ProducerSourcePriority.TIDAL, ProducerSourcePriority.fromSource(tidalSource));
    }

    @Test
    @DisplayName("Should throw exception when converting null Source")
    void shouldThrowExceptionWhenConvertingNullSource() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ProducerSourcePriority.fromSource(null)
        );
        assertTrue(exception.getMessage().contains("cannot be null"));
    }

    @Test
    @DisplayName("Should determine higher priority correctly")
    void shouldDetermineHigherPriorityCorrectly() {
        // MANUAL (1) has higher priority than TIDAL (2)
        assertTrue(ProducerSourcePriority.MANUAL.hasHigherPriorityThan(ProducerSourcePriority.TIDAL));
        assertFalse(ProducerSourcePriority.TIDAL.hasHigherPriorityThan(ProducerSourcePriority.MANUAL));

        // TIDAL (2) has higher priority than SPOTIFY (3)
        assertTrue(ProducerSourcePriority.TIDAL.hasHigherPriorityThan(ProducerSourcePriority.SPOTIFY));
        assertFalse(ProducerSourcePriority.SPOTIFY.hasHigherPriorityThan(ProducerSourcePriority.TIDAL));
    }

    @Test
    @DisplayName("Should handle equal priorities")
    void shouldHandleEqualPriorities() {
        assertFalse(ProducerSourcePriority.MANUAL.hasHigherPriorityThan(ProducerSourcePriority.MANUAL));
        assertFalse(ProducerSourcePriority.TIDAL.hasHigherPriorityThan(ProducerSourcePriority.TIDAL));
    }

    @Test
    @DisplayName("Should throw exception when comparing with null")
    void shouldThrowExceptionWhenComparingWithNull() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ProducerSourcePriority.MANUAL.hasHigherPriorityThan(null)
        );
        assertTrue(exception.getMessage().contains("cannot be null"));
    }

    @Test
    @DisplayName("Should compare priorities correctly")
    void shouldComparePrioritiesCorrectly() {
        // Negative result means higher priority
        assertTrue(ProducerSourcePriority.MANUAL.compareTo(ProducerSourcePriority.TIDAL) < 0);
        assertTrue(ProducerSourcePriority.TIDAL.compareTo(ProducerSourcePriority.MANUAL) > 0);
        assertEquals(0, ProducerSourcePriority.MANUAL.compareTo(ProducerSourcePriority.MANUAL));
    }

    @Test
    @DisplayName("Should throw exception when comparing by priority with null")
    void shouldThrowExceptionWhenComparingByPriorityWithNull() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ProducerSourcePriority.MANUAL.compareByPriority(null)
        );
        assertTrue(exception.getMessage().contains("cannot be null"));
    }
}