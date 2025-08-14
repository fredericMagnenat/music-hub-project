package com.musichub.artist.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import com.musichub.artist.domain.ports.out.ArtistRepository;
import com.musichub.shared.events.TrackWasRegistered;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.musichub.artist.domain.model.Artist;


import com.musichub.shared.domain.values.ISRC;

@ExtendWith(MockitoExtension.class)
@DisplayName("Artist Service Tests")
class ArtistServiceTest {

    private ArtistService artistService;

    @Mock
    private ArtistRepository artistRepository;

    @BeforeEach
    @DisplayName("Set up test environment")
    void setUp() {
        artistService = new ArtistService(artistRepository);
    }

    @Test
    @DisplayName("Should create new artist when handling track registration for unknown artist")
    void unknownArtist_shouldCreateNewArtistWhenHandlingTrackRegistration() {
        // Given
        String artistName = "The Testers";
        ISRC isrc = new ISRC("DEU630901306");
        TrackWasRegistered event = new TrackWasRegistered(isrc, "Test Track", List.of(artistName));

        when(artistRepository.findByName(artistName)).thenReturn(Optional.empty());

        // When
        artistService.handleTrackRegistration(event);

        // Then
        ArgumentCaptor<Artist> artistCaptor = ArgumentCaptor.forClass(Artist.class);
        verify(artistRepository).save(artistCaptor.capture());

        Artist savedArtist = artistCaptor.getValue();
        assertEquals(artistName, savedArtist.getName());
        assertTrue(savedArtist.getTrackReferences().contains(isrc), "New track reference should be added");
    }

    @Test
    @DisplayName("Should update existing artist when handling track registration")
    void existingArtist_shouldUpdateWhenHandlingTrackRegistration() {
        // Given
        String artistName = "The Veterans";
        ISRC existingIsrc = new ISRC("DEU630901307");
        ISRC newIsrc = new ISRC("DEU630901308");
        TrackWasRegistered event = new TrackWasRegistered(newIsrc, "Another Track", List.of(artistName));

        // Create an artist that already has one track reference
        Artist existingArtist = Artist.createProvisional(artistName);
        existingArtist.addTrackReference(existingIsrc);

        when(artistRepository.findByName(artistName)).thenReturn(Optional.of(existingArtist));

        // When
        artistService.handleTrackRegistration(event);

        // Then
        ArgumentCaptor<Artist> artistCaptor = ArgumentCaptor.forClass(Artist.class);
        verify(artistRepository).save(artistCaptor.capture());

        Artist savedArtist = artistCaptor.getValue();
        assertEquals(artistName, savedArtist.getName());
        assertEquals(2, savedArtist.getTrackReferences().size(), "Should now have two track references");
        assertTrue(savedArtist.getTrackReferences().contains(existingIsrc), "Should still contain the old track reference");
        assertTrue(savedArtist.getTrackReferences().contains(newIsrc), "Should contain the new track reference");
    }

    @Test
    @DisplayName("Should handle empty artist names list gracefully")
    void emptyArtistNames_shouldHandleGracefully() {
        // Given
        ISRC isrc = new ISRC("DEU630901309");
        TrackWasRegistered event = new TrackWasRegistered(isrc, "Track Without Artists", List.of());

        // When
        artistService.handleTrackRegistration(event);

        // Then
        verify(artistRepository, never()).findByName(any());
        verify(artistRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should handle null artist names list gracefully")
    void nullArtistNames_shouldHandleGracefully() {
        // Given
        ISRC isrc = new ISRC("DEU630901310");
        TrackWasRegistered event = new TrackWasRegistered(isrc, "Track With Null Artists", null);

        // When
        artistService.handleTrackRegistration(event);

        // Then
        verify(artistRepository, never()).findByName(any());
        verify(artistRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should process multiple artists from same event")
    void multipleArtists_shouldProcessAllArtists() {
        // Given
        String artist1Name = "First Artist";
        String artist2Name = "Second Artist";
        String artist3Name = "Third Artist";
        ISRC isrc = new ISRC("DEU630901311");
        TrackWasRegistered event = new TrackWasRegistered(isrc, "Collaboration Track", 
            List.of(artist1Name, artist2Name, artist3Name));

        // Mock existing artist for artist2
        Artist existingArtist = Artist.createProvisional(artist2Name);
        existingArtist.addTrackReference(new ISRC("DEU630901312"));

        when(artistRepository.findByName(artist1Name)).thenReturn(Optional.empty());
        when(artistRepository.findByName(artist2Name)).thenReturn(Optional.of(existingArtist));
        when(artistRepository.findByName(artist3Name)).thenReturn(Optional.empty());

        // When
        artistService.handleTrackRegistration(event);

        // Then
        verify(artistRepository, times(3)).findByName(any());
        verify(artistRepository).findByName(artist1Name);
        verify(artistRepository).findByName(artist2Name);
        verify(artistRepository).findByName(artist3Name);

        ArgumentCaptor<Artist> artistCaptor = ArgumentCaptor.forClass(Artist.class);
        verify(artistRepository, times(3)).save(artistCaptor.capture());

        List<Artist> savedArtists = artistCaptor.getAllValues();
        assertEquals(3, savedArtists.size());

        // Verify first artist (new)
        Artist savedArtist1 = savedArtists.stream()
            .filter(artist -> artist.getName().equals(artist1Name))
            .findFirst()
            .orElseThrow();
        assertEquals(artist1Name, savedArtist1.getName());
        assertEquals(1, savedArtist1.getTrackReferences().size());
        assertTrue(savedArtist1.getTrackReferences().contains(isrc));

        Artist savedArtist2 = savedArtists.stream()
            .filter(artist -> artist.getName().equals(artist2Name))
            .findFirst()
            .orElseThrow();
        assertEquals(artist2Name, savedArtist2.getName());
        assertEquals(2, savedArtist2.getTrackReferences().size());
        assertTrue(savedArtist2.getTrackReferences().contains(isrc));


        Artist savedArtist3 = savedArtists.stream()
            .filter(artist -> artist.getName().equals(artist3Name))
            .findFirst()
            .orElseThrow();
        assertEquals(artist3Name, savedArtist3.getName());
        assertEquals(1, savedArtist3.getTrackReferences().size());
        assertTrue(savedArtist3.getTrackReferences().contains(isrc));
    }

    @Test
    @DisplayName("Should handle artist name with whitespace gracefully")
    void artistNameWithWhitespace_shouldHandleGracefully() {
        // Given
        String artistNameWithSpaces = "  Artist With Spaces  ";
        ISRC isrc = new ISRC("DEU630901313");
        TrackWasRegistered event = new TrackWasRegistered(isrc, "Spaced Track", List.of(artistNameWithSpaces));

        when(artistRepository.findByName(artistNameWithSpaces)).thenReturn(Optional.empty());

        // When
        artistService.handleTrackRegistration(event);

        // Then
        verify(artistRepository).findByName(artistNameWithSpaces);
        
        ArgumentCaptor<Artist> artistCaptor = ArgumentCaptor.forClass(Artist.class);
        verify(artistRepository).save(artistCaptor.capture());

        Artist savedArtist = artistCaptor.getValue();
        assertEquals(artistNameWithSpaces, savedArtist.getName());
        assertTrue(savedArtist.getTrackReferences().contains(isrc));
    }

    @Test
    @DisplayName("Should not save artist when track reference already exists")
    void existingTrackReference_shouldStillSaveArtist() {
        // Given
        String artistName = "Duplicate Track Artist";
        ISRC duplicateIsrc = new ISRC("DEU630901314");
        TrackWasRegistered event = new TrackWasRegistered(duplicateIsrc, "Duplicate Track", List.of(artistName));

        // Artist already has this track reference
        Artist existingArtist = Artist.createProvisional(artistName);
        existingArtist.addTrackReference(duplicateIsrc);

        when(artistRepository.findByName(artistName)).thenReturn(Optional.of(existingArtist));

        // When
        artistService.handleTrackRegistration(event);

        // Then
        ArgumentCaptor<Artist> artistCaptor = ArgumentCaptor.forClass(Artist.class);
        verify(artistRepository).save(artistCaptor.capture());

        Artist savedArtist = artistCaptor.getValue();
        assertEquals(artistName, savedArtist.getName());
        assertEquals(1, savedArtist.getTrackReferences().size());
        assertTrue(savedArtist.getTrackReferences().contains(duplicateIsrc));
    }
}