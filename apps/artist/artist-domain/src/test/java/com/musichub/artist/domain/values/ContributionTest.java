package com.musichub.artist.domain.values;

import com.musichub.shared.domain.id.TrackId;
import com.musichub.shared.domain.values.ISRC;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Contribution Value Object Tests")
class ContributionTest {

    @Nested
    @DisplayName("Construction")
    class Construction {

        @Test
        @DisplayName("Should create contribution with valid parameters")
        void shouldCreateContributionWithValidParameters() {
            // Given
            TrackId trackId = new TrackId(UUID.randomUUID());
            String title = "Test Track";
            ISRC isrc = ISRC.of("FRLA12400001");

            // When
            Contribution contribution = new Contribution(trackId, title, isrc);

            // Then
            assertThat(contribution.trackId()).isEqualTo(trackId);
            assertThat(contribution.title()).isEqualTo("Test Track");
            assertThat(contribution.isrc()).isEqualTo(isrc);
        }

        @Test
        @DisplayName("Should create contribution using factory method")
        void shouldCreateContributionUsingFactoryMethod() {
            // Given
            TrackId trackId = new TrackId(UUID.randomUUID());
            String title = "Test Track";
            ISRC isrc = ISRC.of("FRLA12400001");

            // When
            Contribution contribution = Contribution.of(trackId, title, isrc);

            // Then
            assertThat(contribution.trackId()).isEqualTo(trackId);
            assertThat(contribution.title()).isEqualTo("Test Track");
            assertThat(contribution.isrc()).isEqualTo(isrc);
        }

        @Test
        @DisplayName("Should trim whitespace from title")
        void shouldTrimWhitespaceFromTitle() {
            // Given
            TrackId trackId = new TrackId(UUID.randomUUID());
            String title = "  Test Track  ";
            ISRC isrc = ISRC.of("FRLA12400001");

            // When
            Contribution contribution = new Contribution(trackId, title, isrc);

            // Then
            assertThat(contribution.title()).isEqualTo("Test Track");
        }
    }

    @Nested
    @DisplayName("Validation")
    class Validation {

        @Test
        @DisplayName("Should throw exception when trackId is null")
        void shouldThrowExceptionWhenTrackIdIsNull() {
            // Given
            TrackId trackId = null;
            String title = "Test Track";
            ISRC isrc = ISRC.of("FRLA12400001");

            // When & Then
            assertThatThrownBy(() -> new Contribution(trackId, title, isrc))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("trackId cannot be null");
        }

        @Test
        @DisplayName("Should throw exception when title is null")
        void shouldThrowExceptionWhenTitleIsNull() {
            // Given
            TrackId trackId = new TrackId(UUID.randomUUID());
            String title = null;
            ISRC isrc = ISRC.of("FRLA12400001");

            // When & Then
            assertThatThrownBy(() -> new Contribution(trackId, title, isrc))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("title cannot be null");
        }

        @Test
        @DisplayName("Should throw exception when isrc is null")
        void shouldThrowExceptionWhenIsrcIsNull() {
            // Given
            TrackId trackId = new TrackId(UUID.randomUUID());
            String title = "Test Track";
            ISRC isrc = null;

            // When & Then
            assertThatThrownBy(() -> new Contribution(trackId, title, isrc))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("isrc cannot be null");
        }

        @Test
        @DisplayName("Should throw exception when title is blank")
        void shouldThrowExceptionWhenTitleIsBlank() {
            // Given
            TrackId trackId = new TrackId(UUID.randomUUID());
            String title = "   ";
            ISRC isrc = ISRC.of("FRLA12400001");

            // When & Then
            assertThatThrownBy(() -> new Contribution(trackId, title, isrc))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("title cannot be blank");
        }

        @Test
        @DisplayName("Should throw exception when title is empty")
        void shouldThrowExceptionWhenTitleIsEmpty() {
            // Given
            TrackId trackId = new TrackId(UUID.randomUUID());
            String title = "";
            ISRC isrc = ISRC.of("FRLA12400001");

            // When & Then
            assertThatThrownBy(() -> new Contribution(trackId, title, isrc))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("title cannot be blank");
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle special characters in title")
        void shouldHandleSpecialCharactersInTitle() {
            // Given
            TrackId trackId = new TrackId(UUID.randomUUID());
            String titleWithSpecialChars = "Señorita (Remix) [2023]";
            ISRC isrc = ISRC.of("FRLA12400001");

            // When
            Contribution contribution = Contribution.of(trackId, titleWithSpecialChars, isrc);

            // Then
            assertThat(contribution.title()).isEqualTo("Señorita (Remix) [2023]");
        }

        @Test
        @DisplayName("Should preserve internal spaces in title")
        void shouldPreserveInternalSpacesInTitle() {
            // Given
            TrackId trackId = new TrackId(UUID.randomUUID());
            String titleWithSpaces = "Track   With    Spaces";
            ISRC isrc = ISRC.of("FRLA12400001");

            // When
            Contribution contribution = Contribution.of(trackId, titleWithSpaces, isrc);

            // Then
            assertThat(contribution.title()).isEqualTo("Track   With    Spaces");
        }

        @Test
        @DisplayName("Should handle long titles")
        void shouldHandleLongTitles() {
            // Given
            TrackId trackId = new TrackId(UUID.randomUUID());
            String longTitle = "A".repeat(500); // Very long title
            ISRC isrc = ISRC.of("FRLA12400001");

            // When
            Contribution contribution = Contribution.of(trackId, longTitle, isrc);

            // Then
            assertThat(contribution.title())
                .hasSize(500)
                .isEqualTo(longTitle);
        }
    }

    @Nested
    @DisplayName("Business Logic")
    class BusinessLogic {

        @Test
        @DisplayName("Should represent artist contribution to track correctly")
        void shouldRepresentArtistContributionToTrackCorrectly() {
            // Given
            TrackId trackId = new TrackId(UUID.randomUUID());
            String title = "Bohemian Rhapsody";
            ISRC isrc = ISRC.of("GBUM71505078");

            // When
            Contribution contribution = Contribution.of(trackId, title, isrc);

            // Then
            assertThat(contribution)
                .satisfies(c -> {
                    assertThat(c.trackId()).isNotNull();
                    assertThat(c.title()).isNotBlank();
                    assertThat(c.isrc()).isNotNull();
                })
                .extracting(Contribution::trackId, Contribution::title, Contribution::isrc)
                .containsExactly(trackId, title, isrc);
        }
    }

    @Nested
    @DisplayName("Equality and Immutability")
    class EqualityAndImmutability {

        @Test
        @DisplayName("Should be equal when all fields are equal")
        void shouldBeEqualWhenAllFieldsAreEqual() {
            // Given
            TrackId trackId = new TrackId(UUID.randomUUID());
            String title = "Test Track";
            ISRC isrc = ISRC.of("FRLA12400001");

            // When
            Contribution contribution1 = new Contribution(trackId, title, isrc);
            Contribution contribution2 = new Contribution(trackId, title, isrc);

            // Then
            assertThat(contribution1)
                .isEqualTo(contribution2)
                .hasSameHashCodeAs(contribution2);
        }

        @Test
        @DisplayName("Should not be equal when trackId differs")
        void shouldNotBeEqualWhenTrackIdDiffers() {
            // Given
            TrackId trackId1 = new TrackId(UUID.randomUUID());
            TrackId trackId2 = new TrackId(UUID.randomUUID());
            String title = "Test Track";
            ISRC isrc = ISRC.of("FRLA12400001");

            // When
            Contribution contribution1 = new Contribution(trackId1, title, isrc);
            Contribution contribution2 = new Contribution(trackId2, title, isrc);

            // Then
            assertThat(contribution1).isNotEqualTo(contribution2);
        }

        @Test
        @DisplayName("Should not be equal when title differs")
        void shouldNotBeEqualWhenTitleDiffers() {
            // Given
            TrackId trackId = new TrackId(UUID.randomUUID());
            ISRC isrc = ISRC.of("FRLA12400001");

            // When
            Contribution contribution1 = new Contribution(trackId, "Track One", isrc);
            Contribution contribution2 = new Contribution(trackId, "Track Two", isrc);

            // Then
            assertThat(contribution1).isNotEqualTo(contribution2);
        }

        @Test
        @DisplayName("Should not be equal when ISRC differs")
        void shouldNotBeEqualWhenIsrcDiffers() {
            // Given
            TrackId trackId = new TrackId(UUID.randomUUID());
            String title = "Test Track";

            // When
            Contribution contribution1 = new Contribution(trackId, title, ISRC.of("FRLA12400001"));
            Contribution contribution2 = new Contribution(trackId, title, ISRC.of("USRC17607839"));

            // Then
            assertThat(contribution1).isNotEqualTo(contribution2);
        }

        @Test
        @DisplayName("Should be equal after title normalization")
        void shouldBeEqualAfterTitleNormalization() {
            // Given
            TrackId trackId = new TrackId(UUID.randomUUID());
            ISRC isrc = ISRC.of("FRLA12400001");

            // When
            Contribution contribution1 = new Contribution(trackId, "  Test Track  ", isrc);
            Contribution contribution2 = new Contribution(trackId, "Test Track", isrc);

            // Then
            assertThat(contribution1)
                .isEqualTo(contribution2)
                .hasSameHashCodeAs(contribution2);
        }
    }
}