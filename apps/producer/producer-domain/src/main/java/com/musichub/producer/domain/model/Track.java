package com.musichub.producer.domain.model;

import com.musichub.producer.domain.values.Source;
import com.musichub.shared.domain.values.ISRC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Domain entity representing a Track within the Producer bounded context.
 * Equality is based on ISRC only.
 */
public final class Track {

    private final ISRC isrc;
    private final String title;
    private final List<String> artistNames;
    private final Source source;

    public Track(ISRC isrc, String title, List<String> artistNames, Source source) {
        this.isrc = Objects.requireNonNull(isrc, "ISRC must not be null");
        this.title = validateNonBlank(title, "title");
        this.artistNames = validateArtistNames(artistNames);
        this.source = Objects.requireNonNull(source, "source must not be null");
    }

    public static Track of(ISRC isrc, String title, List<String> artistNames, Source source) {
        return new Track(isrc, title, artistNames, source);
    }

    public ISRC isrc() { return isrc; }
    public String title() { return title; }
    public List<String> artistNames() { return Collections.unmodifiableList(artistNames); }
    public Source source() { return source; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Track track = (Track) o;
        return normalizeIsrcString(isrc.value()).equals(normalizeIsrcString(track.isrc.value()));
    }

    @Override
    public int hashCode() {
        return normalizeIsrcString(isrc.value()).hashCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Track.class.getSimpleName() + "[", "]")
                .add("isrc=" + isrc.value())
                .add("title='" + title + '\'')
                .add("artistNames=" + artistNames)
                .add("source=" + source)
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

    private static String normalizeIsrcString(String input) {
        return input.replace("-", "").trim().toUpperCase();
    }
}
