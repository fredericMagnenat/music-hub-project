package com.musichub.producer.adapter.rest.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.musichub.producer.adapter.rest.dto.response.ArtistCreditResponse;
import com.musichub.producer.adapter.rest.dto.response.ProducerResponse;
import com.musichub.producer.adapter.rest.dto.response.SourceResponse;
import com.musichub.producer.adapter.rest.dto.response.TrackResponse;
import com.musichub.producer.domain.model.Producer;
import com.musichub.producer.domain.model.Track;
import com.musichub.producer.domain.values.ArtistCredit;
import com.musichub.producer.domain.values.ProducerId;
import com.musichub.producer.domain.values.TrackStatus;
import com.musichub.shared.domain.id.ArtistId;
import com.musichub.shared.domain.values.ISRC;
import com.musichub.shared.domain.values.ProducerCode;
import com.musichub.shared.domain.values.Source;

@DisplayName("ProducerMapper Unit Tests")
class ProducerMapperTest {

    private ProducerMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(ProducerMapper.class);
    }

    @Test
    @DisplayName("Should map Producer to ProducerResponse correctly")
    void toResponse_shouldMapProducerCorrectly() {
        // Given
        ProducerId producerId = ProducerId.fromProducerCode(ProducerCode.of("FRLA1"));
        ProducerCode producerCode = ProducerCode.of("FRLA1");
        String producerName = "Test Producer";

        ISRC isrc = ISRC.of("FRLA12400001");
        String trackTitle = "Test Track";
        ArtistCredit credit1 = ArtistCredit.with("Artist One", ArtistId.fromName("Artist One"));
        ArtistCredit credit2 = ArtistCredit.withName("Artist Two");
        List<ArtistCredit> credits = List.of(credit1, credit2);
        Source source = Source.of("SPOTIFY", "spotify_track_123");
        List<Source> sources = List.of(source);
        Track track = Track.of(isrc, trackTitle, credits, sources, TrackStatus.PROVISIONAL);

        Set<Track> tracks = new LinkedHashSet<>();
        tracks.add(track);

        Producer producer = Producer.from(producerId, producerCode, producerName, tracks);

        // When
        ProducerResponse response = mapper.toResponse(producer);

        // Then
        assertThat(response.id).isEqualTo(producerId.value().toString());
        assertThat(response.producerCode).isEqualTo(producerCode.value());
        assertThat(response.name).isEqualTo(producerName);
        assertThat(response.tracks).hasSize(1);

        TrackResponse trackResponse = response.tracks.iterator().next();
        assertThat(trackResponse.isrc).isEqualTo(isrc.value());
        assertThat(trackResponse.title).isEqualTo(trackTitle);
        assertThat(trackResponse.status).isEqualTo(TrackStatus.PROVISIONAL.name());
        assertThat(trackResponse.credits).hasSize(2);
        assertThat(trackResponse.sources).hasSize(1);
    }

    @Test
    @DisplayName("Should map Producer with null name to ProducerResponse")
    void toResponse_shouldHandleNullProducerName() {
        // Given
        ProducerId producerId = ProducerId.fromProducerCode(ProducerCode.of("FRLA1"));
        ProducerCode producerCode = ProducerCode.of("FRLA1");
        Producer producer = Producer.from(producerId, producerCode, null, Set.of());

        // When
        ProducerResponse response = mapper.toResponse(producer);

        // Then
        assertThat(response.name).isNull();
    }

    @Test
    @DisplayName("Should map Producer with empty tracks to ProducerResponse")
    void toResponse_shouldHandleEmptyTracks() {
        // Given
        ProducerId producerId = ProducerId.fromProducerCode(ProducerCode.of("FRLA1"));
        ProducerCode producerCode = ProducerCode.of("FRLA1");
        String producerName = "Test Producer";
        Producer producer = Producer.from(producerId, producerCode, producerName, Set.of());

        // When
        ProducerResponse response = mapper.toResponse(producer);

        // Then
        assertThat(response.tracks).isEmpty();
    }

    @Test
    @DisplayName("Should map Track to TrackResponse correctly")
    void mapTrack_shouldMapTrackCorrectly() {
        // Given
        ISRC isrc = ISRC.of("FRLA12400001");
        String trackTitle = "Test Track";
        ArtistCredit credit1 = ArtistCredit.with("Artist One", ArtistId.fromName("Artist One"));
        ArtistCredit credit2 = ArtistCredit.withName("Artist Two");
        List<ArtistCredit> credits = List.of(credit1, credit2);
        Source source1 = Source.of("SPOTIFY", "spotify_track_123");
        Source source2 = Source.of("TIDAL", "tidal_track_456");
        List<Source> sources = List.of(source1, source2);
        Track track = Track.of(isrc, trackTitle, credits, sources, TrackStatus.VERIFIED);

        // When
        TrackResponse response = mapper.mapTrack(track);

        // Then
        assertThat(response.isrc).isEqualTo(isrc.value());
        assertThat(response.title).isEqualTo(trackTitle);
        assertThat(response.status).isEqualTo(TrackStatus.VERIFIED.name());
        assertThat(response.credits).hasSize(2);
        assertThat(response.sources).hasSize(2);
    }

    @Test
    @DisplayName("Should map ArtistCredit to ArtistCreditResponse correctly")
    void mapArtistCredit_shouldMapWithId() {
        // Given
        ArtistId artistId = ArtistId.fromName("Test Artist");
        ArtistCredit credit = ArtistCredit.with("Test Artist", artistId);

        // When
        ArtistCreditResponse response = mapper.mapArtistCredit(credit);

        // Then
        assertThat(response.artistName).isEqualTo("Test Artist");
        assertThat(response.artistId).isEqualTo(artistId.value().toString());
    }

    @Test
    @DisplayName("Should map ArtistCredit without ID to ArtistCreditResponse")
    void mapArtistCredit_shouldMapWithoutId() {
        // Given
        ArtistCredit credit = ArtistCredit.withName("Test Artist");

        // When
        ArtistCreditResponse response = mapper.mapArtistCredit(credit);

        // Then
        assertThat(response.artistName).isEqualTo("Test Artist");
        assertThat(response.artistId).isNull();
    }

    @Test
    @DisplayName("Should map Source to SourceResponse correctly")
    void mapSource_shouldMapCorrectly() {
        // Given
        Source source = Source.of("SPOTIFY", "spotify_track_123");

        // When
        SourceResponse response = mapper.mapSource(source);

        // Then
        assertThat(response.name).isEqualTo("SPOTIFY");
        assertThat(response.id).isEqualTo("spotify_track_123");
    }

    @Test
    @DisplayName("Should map credits list correctly")
    void mapCredits_shouldMapListCorrectly() {
        // Given
        ArtistCredit credit1 = ArtistCredit.with("Artist One", ArtistId.fromName("Artist One"));
        ArtistCredit credit2 = ArtistCredit.withName("Artist Two");
        List<ArtistCredit> credits = List.of(credit1, credit2);

        // When
        List<ArtistCreditResponse> responses = mapper.mapCredits(credits);

        // Then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).artistName).isEqualTo("Artist One");
        assertThat(responses.get(1).artistName).isEqualTo("Artist Two");
        assertThat(responses.get(1).artistId).isNull();
    }

    @Test
    @DisplayName("Should map sources list correctly")
    void mapSources_shouldMapListCorrectly() {
        // Given
        Source source1 = Source.of("SPOTIFY", "spotify_track_123");
        Source source2 = Source.of("TIDAL", "tidal_track_456");
        List<Source> sources = List.of(source1, source2);

        // When
        List<SourceResponse> responses = mapper.mapSources(sources);

        // Then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).name).isEqualTo("SPOTIFY");
        assertThat(responses.get(0).id).isEqualTo("spotify_track_123");
        assertThat(responses.get(1).name).isEqualTo("TIDAL");
        assertThat(responses.get(1).id).isEqualTo("tidal_track_456");
    }

    @Test
    @DisplayName("Should map tracks set correctly")
    void mapTracks_shouldMapSetCorrectly() {
        // Given
        ISRC isrc1 = ISRC.of("FRLA12400001");
        ISRC isrc2 = ISRC.of("FRLA12400002");
        Track track1 = Track.of(isrc1, "Track One", List.of(ArtistCredit.withName("Artist")),
                List.of(Source.of("SPOTIFY", "id1")), TrackStatus.PROVISIONAL);
        Track track2 = Track.of(isrc2, "Track Two", List.of(ArtistCredit.withName("Artist")),
                List.of(Source.of("TIDAL", "id2")), TrackStatus.VERIFIED);
        Set<Track> tracks = Set.of(track1, track2);

        // When
        Set<TrackResponse> responses = mapper.mapTracks(tracks);

        // Then
        assertThat(responses).hasSize(2);
        // Verify both tracks are mapped (order doesn't matter for Set)
        List<String> isrcs = responses.stream().map(r -> r.isrc).toList();
        assertThat(isrcs).containsExactlyInAnyOrder("FRLA12400001", "FRLA12400002");
    }
}