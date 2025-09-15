package com.musichub.artist.domain.values;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ArtistName Value Object Tests")
class ArtistNameTest {

    @Nested
    @DisplayName("Construction")
    class Construction {

        @Test
        @DisplayName("Should create artist name with valid name")
        void shouldCreateArtistNameWithValidName() {
            // Given
            String name = "The Beatles";

            // When
            ArtistName artistName = new ArtistName(name);

            // Then
            assertThat(artistName.value()).isEqualTo("The Beatles");
        }

        @Test
        @DisplayName("Should create artist name using factory method")
        void shouldCreateArtistNameUsingFactoryMethod() {
            // Given
            String name = "Bob Dylan";

            // When
            ArtistName artistName = ArtistName.of(name);

            // Then
            assertThat(artistName.value()).isEqualTo("Bob Dylan");
        }

        @Test
        @DisplayName("Should trim whitespace from name")
        void shouldTrimWhitespaceFromName() {
            // Given
            String name = "  Pink Floyd  ";

            // When
            ArtistName artistName = new ArtistName(name);

            // Then
            assertThat(artistName.value()).isEqualTo("Pink Floyd");
        }

        @Test
        @DisplayName("Should preserve internal spaces in name")
        void shouldPreserveInternalSpacesInName() {
            // Given
            String name = "Led  Zeppelin";

            // When
            ArtistName artistName = new ArtistName(name);

            // Then
            assertThat(artistName.value()).isEqualTo("Led  Zeppelin");
        }
    }

    @Nested
    @DisplayName("Validation")
    class Validation {

        @Test
        @DisplayName("Should throw exception when name is null")
        void shouldThrowExceptionWhenNameIsNull() {
            // Given
            String name = null;

            // When & Then
            assertThatThrownBy(() -> new ArtistName(name))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Artist name cannot be null");
        }

        @Test
        @DisplayName("Should throw exception when name is blank")
        void shouldThrowExceptionWhenNameIsBlank() {
            // Given
            String name = "   ";

            // When & Then
            assertThatThrownBy(() -> new ArtistName(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Artist name cannot be blank");
        }

        @Test
        @DisplayName("Should throw exception when name is empty")
        void shouldThrowExceptionWhenNameIsEmpty() {
            // Given
            String name = "";

            // When & Then
            assertThatThrownBy(() -> new ArtistName(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Artist name cannot be blank");
        }

        @Test
        @DisplayName("Should throw exception when name exceeds 255 characters")
        void shouldThrowExceptionWhenNameExceeds255Characters() {
            // Given
            String name = "A".repeat(256);

            // When & Then
            assertThatThrownBy(() -> new ArtistName(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Artist name cannot exceed 255 characters");
        }

        @Test
        @DisplayName("Should accept name with exactly 255 characters")
        void shouldAcceptNameWithExactly255Characters() {
            // Given
            String name = "A".repeat(255);

            // When
            ArtistName artistName = new ArtistName(name);

            // Then
            assertThat(artistName.value()).hasSize(255);
        }
    }

    @Nested
    @DisplayName("String Conversion")
    class StringConversion {

        @Test
        @DisplayName("Should return name value when toString is called")
        void shouldReturnNameValueWhenToStringIsCalled() {
            // Given
            String name = "Queen";
            ArtistName artistName = new ArtistName(name);

            // When
            String result = artistName.toString();

            // Then
            assertThat(result).isEqualTo("Queen");
        }
    }

    @Nested
    @DisplayName("Equality and Immutability")
    class EqualityAndImmutability {

        @Test
        @DisplayName("Should be equal when names are equal")
        void shouldBeEqualWhenNamesAreEqual() {
            // Given
            String name = "AC/DC";

            // When
            ArtistName artistName1 = new ArtistName(name);
            ArtistName artistName2 = new ArtistName(name);

            // Then
            assertThat(artistName1)
                .isEqualTo(artistName2)
                .hasSameHashCodeAs(artistName2);
        }

        @Test
        @DisplayName("Should not be equal when names differ")
        void shouldNotBeEqualWhenNamesDiffer() {
            // Given
            ArtistName artistName1 = new ArtistName("Metallica");
            ArtistName artistName2 = new ArtistName("Iron Maiden");

            // When & Then
            assertThat(artistName1).isNotEqualTo(artistName2);
        }

        @Test
        @DisplayName("Should be equal after normalization")
        void shouldBeEqualAfterNormalization() {
            // Given
            ArtistName artistName1 = new ArtistName("  Beatles  ");
            ArtistName artistName2 = new ArtistName("Beatles");

            // When & Then
            assertThat(artistName1)
                .isEqualTo(artistName2)
                .hasSameHashCodeAs(artistName2);
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @ParameterizedTest(name = "Should handle {0}")
        @MethodSource("specialCharacterTestCases")
        @DisplayName("Should handle various special character cases")
        void shouldHandleSpecialCharacterCases(String description, String input, String expected) {
            // When
            ArtistName artistName = ArtistName.of(input);

            // Then
            assertThat(artistName.value()).isEqualTo(expected);
        }

        private static Stream<Arguments> specialCharacterTestCases() {
            return Stream.of(
                Arguments.of("special characters correctly", "Sigur Rós & Björk", "Sigur Rós & Björk"),
                Arguments.of("Unicode characters", "东方神起", "东方神起"),
                Arguments.of("mixed whitespace types", "\tQueen\n", "Queen")
            );
        }
    }
}