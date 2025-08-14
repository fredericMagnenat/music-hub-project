package com.musichub.producer.domain.model;

import com.musichub.producer.domain.values.ProducerId;
import com.musichub.shared.domain.values.ISRC;
import com.musichub.shared.domain.values.ProducerCode;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Producer aggregate root. Owns a set of Tracks identified by ISRC.
 */
public final class Producer {

    private final ProducerId id;
    private final ProducerCode producerCode;
    private String name; // optional, mutable business attribute
    private final Set<ISRC> tracks; // normalized ISRCs only

    private Producer(ProducerId id, ProducerCode producerCode, String name, Set<ISRC> tracks) {
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

    public ProducerId id() { return id; }

    public ProducerCode producerCode() { return producerCode; }

    public String name() { return name; }

    public void rename(String newName) { this.name = newName; }

    public Set<ISRC> tracks() { return Collections.unmodifiableSet(tracks); }

    public boolean hasTrack(ISRC isrc) {
        Objects.requireNonNull(isrc, "ISRC must not be null");
        return tracks.contains(normalize(isrc));
    }

    public boolean addTrack(ISRC isrc) {
        Objects.requireNonNull(isrc, "ISRC must not be null");
        ISRC normalized = normalize(isrc);
        return tracks.add(normalized); // idempotent due to Set
    }

    public boolean addTrack(String isrcValue) {
        Objects.requireNonNull(isrcValue, "isrcValue must not be null");
        ISRC normalized = ISRC.of(normalizeIsrcString(isrcValue));
        return addTrack(normalized);
    }

    private static ISRC normalize(ISRC isrc) {
        return ISRC.of(normalizeIsrcString(isrc.value()));
    }

    private static String normalizeIsrcString(String input) {
        return input.replace("-", "").trim().toUpperCase();
    }
}
