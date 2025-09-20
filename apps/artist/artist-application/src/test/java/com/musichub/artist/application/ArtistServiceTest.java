package com.musichub.artist.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.lenient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.musichub.artist.application.ports.out.ArtistRepository;
import com.musichub.artist.application.service.ArtistService;
import com.musichub.artist.application.service.ArtistEnrichmentService;
import com.musichub.shared.events.ArtistCreditInfo;
import com.musichub.shared.events.SourceInfo;
import com.musichub.shared.events.TrackWasRegistered;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.musichub.artist.domain.model.Artist;
import com.musichub.artist.domain.values.Contribution;
import com.musichub.shared.domain.id.TrackId;
import com.musichub.shared.domain.values.ISRC;

@ExtendWith(MockitoExtension.class)
@DisplayName("Artist Service Tests")
class ArtistServiceTest {

    private ArtistService artistService;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private ArtistEnrichmentService enrichmentService;

    @BeforeEach
    @DisplayName("Set up test environment")
    void setUp() {
        artistService = new ArtistService(artistRepository, enrichmentService);
        // Setup default enrichment service behavior - use lenient to avoid UnnecessaryStubbingException
        lenient().when(enrichmentService.enrichArtist(any(Artist.class)))
            .thenReturn(CompletableFuture.completedFuture(mock(Artist.class)));
    }

    @Test
    @DisplayName("Should create new artist when handling track registration for unknown artist")
    void unknownArtist_shouldCreateNewArtistWhenHandlingTrackRegistration() {
        // Given
        String artistName = "The Testers";
        ISRC isrc = ISRC.of("DEU630901306");
        UUID producerId = UUID.randomUUID();
        TrackWasRegistered event = new TrackWasRegistered(
            isrc,
            "Test Track",
            producerId,
            List.of(ArtistCreditInfo.withName(artistName)),
            List.of(new SourceInfo("SPOTIFY", "spotify123"))
        );

        when(artistRepository.findByName(artistName)).thenReturn(Optional.empty());
        when(artistRepository.save(any(Artist.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        artistService.handleTrackRegistration(event);

        // Then
        ArgumentCaptor<Artist> artistCaptor = ArgumentCaptor.forClass(Artist.class);
        verify(artistRepository, times(2)).save(artistCaptor.capture()); // Once for creation, once for contribution

        List<Artist> savedArtists = artistCaptor.getAllValues();
        Artist finalArtist = savedArtists.get(savedArtists.size() - 1);
        assertEquals(artistName, finalArtist.getNameValue());
        assertEquals(1, finalArtist.getContributions().size());

        Contribution contribution = finalArtist.getContributions().get(0);
        assertEquals("Test Track", contribution.title());
        assertEquals(isrc, contribution.isrc());
    }

    @Test
    @DisplayName("Should update existing artist when handling track registration")
    void existingArtist_shouldUpdateWhenHandlingTrackRegistration() {
        // Given
        String artistName = "The Veterans";
        ISRC existingIsrc = ISRC.of("DEU630901307");
        ISRC newIsrc = ISRC.of("DEU630901308");
        UUID producerId = UUID.randomUUID();
        TrackWasRegistered event = new TrackWasRegistered(
            newIsrc,
            "Another Track",
            producerId,
            List.of(ArtistCreditInfo.withName(artistName)),
            List.of(new SourceInfo("TIDAL", "tidal456"))
        );

        // Create an artist that already has one contribution
        Artist existingArtist = Artist.createProvisional(artistName);
        Contribution existingContribution = Contribution.of(
            new TrackId(UUID.randomUUID()),
            "Existing Track",
            existingIsrc
        );
        existingArtist = existingArtist.addContribution(existingContribution);

        when(artistRepository.findByName(artistName)).thenReturn(Optional.of(existingArtist));
        when(artistRepository.save(any(Artist.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        artistService.handleTrackRegistration(event);

        // Then
        ArgumentCaptor<Artist> artistCaptor = ArgumentCaptor.forClass(Artist.class);
        verify(artistRepository).save(artistCaptor.capture());

        Artist savedArtist = artistCaptor.getValue();
        assertEquals(artistName, savedArtist.getNameValue());
        assertEquals(2, savedArtist.getContributions().size(), "Should now have two contributions");

        // Check both contributions are present
        boolean hasOldContribution = savedArtist.getContributions().stream()
            .anyMatch(c -> c.isrc().equals(existingIsrc));
        boolean hasNewContribution = savedArtist.getContributions().stream()
            .anyMatch(c -> c.isrc().equals(newIsrc) && c.title().equals("Another Track"));

        assertTrue(hasOldContribution, "Should still contain the old contribution");
        assertTrue(hasNewContribution, "Should contain the new contribution");
    }

    @Test
    @DisplayName("Should handle empty artist names list gracefully")
    void emptyArtistNames_shouldHandleGracefully() {
        // Given
        ISRC isrc = ISRC.of("DEU630901309");
        UUID producerId = UUID.randomUUID();
        TrackWasRegistered event = new TrackWasRegistered(
            isrc,
            "Track Without Artists",
            producerId,
            List.of(),
            List.of(new SourceInfo("MANUAL", "manual789"))
        );

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
        ISRC isrc = ISRC.of("DEU630901310");
        UUID producerId = UUID.randomUUID();
        TrackWasRegistered event = new TrackWasRegistered(
            isrc,
            "Track With Null Artists",
            producerId,
            null,
            List.of(new SourceInfo("DEEZER", "deezer101"))
        );

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
        ISRC isrc = ISRC.of("DEU630901311");
        UUID producerId = UUID.randomUUID();
        TrackWasRegistered event = new TrackWasRegistered(
            isrc,
            "Collaboration Track",
            producerId,
            List.of(
                ArtistCreditInfo.withName(artist1Name),
                ArtistCreditInfo.withName(artist2Name),
                ArtistCreditInfo.withName(artist3Name)
            ),
            List.of(new SourceInfo("APPLE_MUSIC", "apple202"))
        );

        // Mock existing artist for artist2
        Artist existingArtist = Artist.createProvisional(artist2Name);
        Contribution existingContribution = Contribution.of(
            new TrackId(UUID.randomUUID()),
            "Existing Track",
            ISRC.of("DEU630901312")
        );
        existingArtist = existingArtist.addContribution(existingContribution);

        when(artistRepository.findByName(artist1Name)).thenReturn(Optional.empty());
        when(artistRepository.findByName(artist2Name)).thenReturn(Optional.of(existingArtist));
        when(artistRepository.findByName(artist3Name)).thenReturn(Optional.empty());
        when(artistRepository.save(any(Artist.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        artistService.handleTrackRegistration(event);

        // Then
        verify(artistRepository, times(3)).findByName(any());
        verify(artistRepository).findByName(artist1Name);
        verify(artistRepository).findByName(artist2Name);
        verify(artistRepository).findByName(artist3Name);

        ArgumentCaptor<Artist> artistCaptor = ArgumentCaptor.forClass(Artist.class);
        verify(artistRepository, times(5)).save(artistCaptor.capture()); // 2 new artists + 3 with contributions

        List<Artist> savedArtists = artistCaptor.getAllValues();

        // Filter to get only artists that have contributions (exclude newly created artists without contributions)
        List<Artist> artistsWithContributions = savedArtists.stream()
            .filter(artist -> !artist.getContributions().isEmpty())
            .toList();

        // Check each artist with contributions has the collaboration contribution
        for (Artist artist : artistsWithContributions) {
            boolean hasCollaborationContribution = artist.getContributions().stream()
                .anyMatch(c -> c.isrc().equals(isrc) && c.title().equals("Collaboration Track"));
            assertTrue(hasCollaborationContribution,
                "Artist " + artist.getNameValue() + " should have collaboration contribution");
        }

        // Verify we have exactly 3 artists with contributions
        assertEquals(3, artistsWithContributions.size(), "Should have exactly 3 artists with contributions");
    }

    @Test
    @DisplayName("Should handle artist name with whitespace gracefully")
    void artistNameWithWhitespace_shouldHandleGracefully() {
        // Given
        String artistNameWithSpaces = "  Artist With Spaces  ";
        ISRC isrc = ISRC.of("DEU630901313");
        UUID producerId = UUID.randomUUID();
        TrackWasRegistered event = new TrackWasRegistered(
            isrc,
            "Spaced Track",
            producerId,
            List.of(ArtistCreditInfo.withName(artistNameWithSpaces)),
            List.of(new SourceInfo("SPOTIFY", "spotify303"))
        );

        when(artistRepository.findByName(artistNameWithSpaces)).thenReturn(Optional.empty());
        when(artistRepository.save(any(Artist.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        artistService.handleTrackRegistration(event);

        // Then
        verify(artistRepository).findByName(artistNameWithSpaces);

        ArgumentCaptor<Artist> artistCaptor = ArgumentCaptor.forClass(Artist.class);
        verify(artistRepository, times(2)).save(artistCaptor.capture());

        List<Artist> savedArtists = artistCaptor.getAllValues();
        Artist finalArtist = savedArtists.get(savedArtists.size() - 1);
        // ArtistName trims whitespace, so expect the trimmed version
        assertEquals(artistNameWithSpaces.trim(), finalArtist.getNameValue());

        boolean hasContribution = finalArtist.getContributions().stream()
            .anyMatch(c -> c.isrc().equals(isrc) && c.title().equals("Spaced Track"));
        assertTrue(hasContribution, "Should contain the track contribution");
    }

    @Test
    @DisplayName("Should handle duplicate contribution idempotently")
    void existingContribution_shouldHandleIdempotently() {
        // Given
        String artistName = "Duplicate Track Artist";
        ISRC duplicateIsrc = ISRC.of("DEU630901314");
        UUID producerId = UUID.randomUUID();
        TrackWasRegistered event = new TrackWasRegistered(
            duplicateIsrc,
            "Duplicate Track",
            producerId,
            List.of(ArtistCreditInfo.withName(artistName)),
            List.of(new SourceInfo("TIDAL", "tidal404"))
        );

        // Artist already has this contribution
        Artist existingArtist = Artist.createProvisional(artistName);
        Contribution existingContribution = Contribution.of(
            new TrackId(producerId),
            "Duplicate Track",
            duplicateIsrc
        );
        existingArtist = existingArtist.addContribution(existingContribution);

        when(artistRepository.findByName(artistName)).thenReturn(Optional.of(existingArtist));
        when(artistRepository.save(any(Artist.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        artistService.handleTrackRegistration(event);

        // Then
        ArgumentCaptor<Artist> artistCaptor = ArgumentCaptor.forClass(Artist.class);
        verify(artistRepository).save(artistCaptor.capture());

        Artist savedArtist = artistCaptor.getValue();
        assertEquals(artistName, savedArtist.getNameValue());
        assertEquals(1, savedArtist.getContributions().size()); // Still only one contribution due to idempotency

        Contribution contribution = savedArtist.getContributions().get(0);
        assertEquals(duplicateIsrc, contribution.isrc());
        assertEquals("Duplicate Track", contribution.title());
    }
}