package com.musichub.artist.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import com.musichub.shared.events.TrackWasRegistered;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.musichub.artist.domain.Artist;
import com.musichub.artist.application.port.out.ArtistRepository;

import com.musichub.shared.domain.values.ISRC;

@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {

    private ArtistService artistService;

    @Mock
    private ArtistRepository artistRepository;

    @BeforeEach
    void setUp() {
        artistService = new ArtistService(artistRepository);
    }

    @Test
    void shouldCreateNewArtistWhenHandlingTrackRegistrationForUnknownArtist() {
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
    void shouldUpdateExistingArtistWhenHandlingTrackRegistration() {
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
}