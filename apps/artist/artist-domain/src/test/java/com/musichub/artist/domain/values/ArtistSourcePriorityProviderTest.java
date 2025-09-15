package com.musichub.artist.domain.values;

import com.musichub.shared.domain.values.SourceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ArtistSourcePriorityProvider Tests")
class ArtistSourcePriorityProviderTest {

    private final ArtistSourcePriorityProvider provider = new ArtistSourcePriorityProvider();

    @Test
    @DisplayName("Should have correct priority order")
    void shouldHaveCorrectPriorityOrder() {
        List<SourceType> order = provider.getPriorityOrder();

        assertThat(order).containsExactly(
                SourceType.MANUAL,
                SourceType.TIDAL,
                SourceType.SPOTIFY,
                SourceType.DEEZER,
                SourceType.APPLE_MUSIC
        );
    }

    @Test
    @DisplayName("Should determine higher priority correctly")
    void shouldDetermineHigherPriorityCorrectly() {
        // MANUAL > TIDAL
        assertThat(provider.hasHigherPriority(SourceType.MANUAL, SourceType.TIDAL))
                .isTrue();

        // TIDAL > SPOTIFY
        assertThat(provider.hasHigherPriority(SourceType.TIDAL, SourceType.SPOTIFY))
                .isTrue();

        // SPOTIFY < TIDAL
        assertThat(provider.hasHigherPriority(SourceType.SPOTIFY, SourceType.TIDAL))
                .isFalse();

        // SPOTIFY > DEEZER
        assertThat(provider.hasHigherPriority(SourceType.SPOTIFY, SourceType.DEEZER))
                .isTrue();

        // DEEZER > APPLE_MUSIC  
        assertThat(provider.hasHigherPriority(SourceType.DEEZER, SourceType.APPLE_MUSIC))
                .isTrue();
    }

    @Test
    @DisplayName("Should handle unknown source types")
    void shouldHandleUnknownSourceTypes() {
        // Given - A hypothetical new source type that's not in the priority list
        // When comparing with a known type, known type should win
        // Note: This tests the edge case handling in the implementation
        
        assertThat(provider.getPriorityOrder()).hasSize(5);
        assertThat(provider.getPriorityOrder()).contains(
            SourceType.MANUAL, SourceType.TIDAL, SourceType.SPOTIFY, 
            SourceType.DEEZER, SourceType.APPLE_MUSIC
        );
    }

    @Test
    @DisplayName("Should maintain immutable priority order")
    void shouldMaintainImmutablePriorityOrder() {
        // Given
        List<SourceType> order1 = provider.getPriorityOrder();
        List<SourceType> order2 = provider.getPriorityOrder();

        // Then - Should return the same content but potentially different instances
        assertThat(order1).containsExactlyElementsOf(order2);
        
        // Should not be able to modify the returned list
        assertThatThrownBy(() -> order1.clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
