package com.musichub.artist;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.musichub.artist.domain.Artist;
import com.musichub.artist.domain.ArtistRepository;
import com.musichub.events.TrackWasRegistered;
import com.musichub.shared.domain.values.ISRC;

import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.Vertx;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

@QuarkusTest
public class ArtistServiceIntegrationTest {

    @Inject
    Vertx vertx;

    @Inject
    ArtistRepository artistRepository;

    @Test
    void shouldCreateArtistWhenTrackIsRegisteredForNewArtist() {
        // Given
        String artistName = "The Newcomers";
        ISRC isrc = new ISRC("DEU630901309");
        TrackWasRegistered event = new TrackWasRegistered(isrc, "First Hit",
                List.of(artistName));

        // When
        vertx.eventBus().publish("track-registered", event);

        // Then
        await().atMost(5, SECONDS).untilAsserted(() -> {
            QuarkusTransaction.requiringNew().run(() -> {
                Optional<Artist> savedArtistOpt = artistRepository.findByName(artistName);
                assertTrue(savedArtistOpt.isPresent(),
                        "Artist should have been created in the database");

                Artist savedArtist = savedArtistOpt.get();
                assertEquals(artistName, savedArtist.getName());
                assertEquals(1, savedArtist.getTrackReferences().size());
                assertTrue(savedArtist.getTrackReferences().contains(isrc));
            });
        });
    }

    @Test
    void shouldUpdateArtistWhenTrackIsRegisteredForExistingArtist() {
        // Given
        String artistName = "The Legends";
        ISRC existingIsrc = new ISRC("DEU630901310");
        ISRC newIsrc = new ISRC("DEU630901311");

        // Pre-populate the database with an existing artist
        QuarkusTransaction.requiringNew().run(() -> {
            Artist existingArtist = Artist.createProvisional(artistName);
            existingArtist.addTrackReference(existingIsrc);
            artistRepository.save(existingArtist);
        });

        // Create the event for the new track
        TrackWasRegistered event = new TrackWasRegistered(newIsrc, "Latest Hit",
                List.of(artistName));

        // When
        vertx.eventBus().publish("track-registered", event);

        // Then
        await().atMost(5, SECONDS).untilAsserted(() -> {
            QuarkusTransaction.requiringNew().run(() -> {
                Optional<Artist> savedArtistOpt = artistRepository.findByName(artistName);
                assertTrue(savedArtistOpt.isPresent());

                Artist savedArtist = savedArtistOpt.get();
                assertEquals(2, savedArtist.getTrackReferences().size(),
                        "Should have two track references");
                assertTrue(savedArtist.getTrackReferences().contains(existingIsrc),
                        "Should still contain the old track reference");
                assertTrue(savedArtist.getTrackReferences().contains(newIsrc),
                        "Should contain the new track reference");
            });
        });
    }

}