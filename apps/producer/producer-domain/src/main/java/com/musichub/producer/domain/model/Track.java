package com.musichub.producer.domain.model;

import com.musichub.producer.domain.values.Source;
import com.musichub.producer.domain.values.TrackStatus;
import com.musichub.shared.domain.values.ISRC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Domain entity representing a Track within the Producer bounded context.
 * Equality is based on ISRC only.
 * 
 * <p>This entity is immutable and thread-safe. All collections are defensively copied
 * and returned as unmodifiable views.</p>
 */
public final class Track {

    private final ISRC isrc;
    private final String title;
    private final List<String> artistNames;
    private final List<Source> sources;
    private final TrackStatus status;

    public Track(ISRC isrc, String title, List<String> artistNames, List<Source> sources, TrackStatus status) {
        this.isrc = Objects.requireNonNull(isrc, "ISRC must not be null");
        this.title = validateNonBlank(title, "title");
        this.artistNames = Collections.unmodifiableList(validateArtistNames(artistNames));
        this.sources = Collections.unmodifiableList(validateSources(sources));
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    /**
     * Factory method to create a new Track instance.
     *
     * @param isrc the unique ISRC identifier
     * @param title the track title (will be trimmed)
     * @param artistNames list of artist names (will be validated and trimmed)
     * @param sources list of sources (must not be empty)
     * @param status the current track status
     * @return a new immutable Track instance
     * @throws IllegalArgumentException if any validation fails
     */
    public static Track of(ISRC isrc, String title, List<String> artistNames, List<Source> sources, TrackStatus status) {
        return new Track(isrc, title, artistNames, sources, status);
    }

    /**
     * @return the unique ISRC identifier for this track
     */
    public ISRC isrc() { return isrc; }
    
    /**
     * @return the track title (trimmed and validated)
     */
    public String title() { return title; }
    
    /**
     * @return an unmodifiable list of artist names
     */
    public List<String> artistNames() { return artistNames; }
    
    /**
     * @return an unmodifiable list of sources
     */
    public List<Source> sources() { return sources; }
    
    /**
     * @return the current status of this track
     */
    public TrackStatus status() { return status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Track track = (Track) o;
        // Normalize ISRC values for comparison to handle different formats
        return normalizeIsrc(isrc.value()).equals(normalizeIsrc(track.isrc.value()));
    }

    @Override
    public int hashCode() {
        return normalizeIsrc(isrc.value()).hashCode();
    }
    
    /**
     * Normalizes ISRC string by removing dashes, trimming, and converting to uppercase.
     * This ensures consistent comparison across different ISRC formats.
     */
    private static String normalizeIsrc(String input) {
        return input.replace("-", "").trim().toUpperCase();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Track.class.getSimpleName() + "[", "]")
                .add("isrc=" + isrc.value())
                .add("title='" + title + '\'')
                .add("artistNames=" + artistNames)
                .add("sources=" + sources)
                .add("status=" + status)
                .toString();
    }

    private static String validateNonBlank(String value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }

    private static List<String> validateArtistNames(List<String> names) {
        if (names == null || names.isEmpty()) {
            throw new IllegalArgumentException("artistNames must not be null or empty");
        }
        List<String> normalized = new ArrayList<>(names.size());
        for (String name : names) {
            String n = validateNonBlank(name, "artistNames element");
            normalized.add(n);
        }
        return normalized;
    }

    private static List<Source> validateSources(List<Source> sources) {
        if (sources == null || sources.isEmpty()) {
            throw new IllegalArgumentException("sources must not be null or empty");
        }
        List<Source> validated = new ArrayList<>(sources.size());
        for (Source source : sources) {
            if (source == null) {
                throw new IllegalArgumentException("sources cannot contain null elements");
            }
            validated.add(source);
        }
        return validated;
    }

}
