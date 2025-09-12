package com.musichub.producer.domain.model;

import com.musichub.producer.domain.values.ArtistCredit;
import com.musichub.producer.domain.values.ProducerId;
import com.musichub.shared.domain.values.Source;
import com.musichub.producer.domain.values.TrackStatus;
import com.musichub.shared.domain.values.ISRC;
import com.musichub.shared.domain.values.ProducerCode;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Producer aggregate root. Owns a set of Track entities with complete metadata.
 */
public final class Producer {

    private static final String ISRC_MUST_NOT_BE_NULL = "ISRC must not be null";

    private final ProducerId id;
    private final ProducerCode producerCode;
    private String name; // optional, mutable business attribute
    private final Set<Track> tracks; // Track entities with complete metadata

    private Producer(ProducerId id, ProducerCode producerCode, String name, Set<Track> tracks) {
        this.id = Objects.requireNonNull(id, "Producer.id must not be null");
        this.producerCode = Objects.requireNonNull(producerCode, "Producer.producerCode must not be null");
        this.name = name; // nullable allowed
        this.tracks = tracks == null ? new LinkedHashSet<>() : new LinkedHashSet<>(tracks);
    }

    public static Producer createNew(ProducerCode producerCode, String name) {
        Objects.requireNonNull(producerCode, "ProducerCode must not be null");
        ProducerId producerId = ProducerId.fromProducerCode(producerCode);
        return new Producer(producerId, producerCode, name, new LinkedHashSet<>());
    }

    public static Producer from(ProducerId id, ProducerCode producerCode, String name, Set<Track> tracks) {
        return new Producer(id, producerCode, name, tracks);
    }

    public ProducerId id() {
        return id;
    }

    public ProducerCode producerCode() {
        return producerCode;
    }

    public String name() {
        return name;
    }

    public void rename(String newName) {
        this.name = newName;
    }

    public Set<Track> tracks() {
        return Collections.unmodifiableSet(tracks);
    }

    public boolean hasTrack(ISRC isrc) {
        Objects.requireNonNull(isrc, ISRC_MUST_NOT_BE_NULL);
        return tracks.stream().anyMatch(track -> normalize(track.isrc()).equals(normalize(isrc)));
    }

    public Optional<Track> getTrack(ISRC isrc) {
        Objects.requireNonNull(isrc, ISRC_MUST_NOT_BE_NULL);
        return tracks.stream()
            .filter(track -> normalize(track.isrc()).equals(normalize(isrc)))
            .findFirst();
    }


    /**
     * Adds a Track entity to this Producer aggregate, enforcing business rules.
     * - The track's producer code (derived from its ISRC) must match this aggregate's producer code
     * - Idempotent behavior: returns false if an equivalent track already exists
     */
    public boolean addTrack(Track track) {
        Objects.requireNonNull(track, "track must not be null");
        // Validate producer code consistency using ISRC-derived ProducerCode
        ProducerCode trackProducerCode = ProducerCode.with(track.isrc());
        if (!this.producerCode.equals(trackProducerCode)) {
            throw new IllegalArgumentException("Track producer code does not match aggregate producer code");
        }
        return tracks.add(track); // idempotent due to Track.equals() based on ISRC
    }

    /**
     * Factory method to register a track with complete metadata in the Producer aggregate.
     * This enforces DDD principles by keeping track creation within the aggregate boundary.
     */
    public boolean registerTrack(ISRC isrc, String title, List<ArtistCredit> credits, List<Source> sources) {
        Objects.requireNonNull(isrc, ISRC_MUST_NOT_BE_NULL);
        Objects.requireNonNull(title, "title must not be null");
        Objects.requireNonNull(credits, "credits must not be null");
        Objects.requireNonNull(sources, "sources must not be null");

        // Validate producer code consistency using ISRC-derived ProducerCode
        ProducerCode trackProducerCode = ProducerCode.with(isrc);
        if (!this.producerCode.equals(trackProducerCode)) {
            throw new IllegalArgumentException("Track producer code does not match aggregate producer code");
        }

        Track track = Track.of(normalize(isrc), title, credits, sources, TrackStatus.PROVISIONAL);
        return tracks.add(track);
    }

    /**
     * Convenience factory method to register a track with artist names only.
     */
    public boolean registerTrackWithArtistNames(ISRC isrc, String title, List<String> artistNames, List<Source> sources) {
        Objects.requireNonNull(artistNames, "artistNames must not be null");
        List<ArtistCredit> credits = artistNames.stream().map(ArtistCredit::withName).toList();
        return registerTrack(isrc, title, credits, sources);
    }

    private static ISRC normalize(ISRC isrc) {
        return ISRC.of(normalizeIsrcString(isrc.value()));
    }

    private static String normalizeIsrcString(String input) {
        return input.replace("-", "").trim().toUpperCase();
    }
}
