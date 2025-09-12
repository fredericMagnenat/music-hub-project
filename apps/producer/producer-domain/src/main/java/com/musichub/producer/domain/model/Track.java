package com.musichub.producer.domain.model;

import com.musichub.producer.domain.values.ArtistCredit;
import com.musichub.shared.domain.values.Source;
import com.musichub.producer.domain.values.ProducerSourcePriority;
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
    private final List<ArtistCredit> credits;
    private final List<Source> sources;
    private final TrackStatus status;

    public Track(ISRC isrc, String title, List<ArtistCredit> credits, List<Source> sources, TrackStatus status) {
        this.isrc = Objects.requireNonNull(isrc, "ISRC must not be null");
        this.title = validateNonBlank(title, "title");
        this.credits = Collections.unmodifiableList(validateCredits(credits));
        this.sources = Collections.unmodifiableList(validateSources(sources));
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    /**
     * Factory method to create a new Track instance.
     *
     * @param isrc the unique ISRC identifier
     * @param title the track title (will be trimmed)
     * @param credits list of artist credits (will be validated)
     * @param sources list of sources (must not be empty)
     * @param status the current track status
     * @return a new immutable Track instance
     * @throws IllegalArgumentException if any validation fails
     */
    public static Track of(ISRC isrc, String title, List<ArtistCredit> credits, List<Source> sources, TrackStatus status) {
        return new Track(isrc, title, credits, sources, status);
    }
    
    /**
     * Convenience factory method to create a Track with artist names (no IDs).
     * Converts string names to ArtistCredit instances.
     *
     * @param isrc the unique ISRC identifier
     * @param title the track title
     * @param artistNames list of artist names
     * @param sources list of sources
     * @param status the current track status
     * @return a new Track instance
     */
    public static Track withArtistNames(ISRC isrc, String title, List<String> artistNames, List<Source> sources, TrackStatus status) {
        List<ArtistCredit> credits = artistNames.stream()
                .map(ArtistCredit::withName)
                .toList();
        return new Track(isrc, title, credits, sources, status);
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
     * @return an unmodifiable list of artist credits
     */
    public List<ArtistCredit> credits() { return credits; }
    
    /**
     * Convenience method to get artist names as strings.
     * @return list of artist names from credits
     */
    public List<String> artistNames() { 
        return credits.stream()
                .map(ArtistCredit::artistName)
                .toList();
    }
    
    /**
     * @return an unmodifiable list of sources
     */
    public List<Source> sources() { return sources; }
    
    /**
     * @return the current status of this track
     */
    public TrackStatus status() { return status; }
    
    /**
     * Gets the highest priority source according to the Source of Truth Hierarchy.
     * Returns the source with the highest priority (lowest numerical priority value).
     *
     * @return the highest priority source, or first source if only one exists
     */
    public Source getHighestPrioritySource() {
        return sources.stream()
            .min((s1, s2) -> {
                ProducerSourcePriority p1 = ProducerSourcePriority.fromSource(s1);
                ProducerSourcePriority p2 = ProducerSourcePriority.fromSource(s2);
                return Integer.compare(p1.getPriorityValue(), p2.getPriorityValue());
            })
            .orElseThrow(() -> new IllegalStateException("Track must have at least one source"));
    }
    
    /**
     * Creates an updated version of this track with new metadata, applying the Source of Truth Hierarchy.
     * Convenience method that converts artist names to ArtistCredit objects.
     * 
     * @param newTitle the new title (may be null to keep current)
     * @param newArtistNames the new artist names (may be null to keep current)
     * @param newSource the new source providing this data (must not be null)
     * @param newStatus the new status (may be null to keep current)
     * @return a new Track instance with updated data if source priority allows
     */
    public Track updateWithArtistNames(String newTitle, List<String> newArtistNames, Source newSource, TrackStatus newStatus) {
        List<ArtistCredit> newCredits = newArtistNames != null ? 
                newArtistNames.stream().map(ArtistCredit::withName).toList() : null;
        return updateWithSourcePriority(newTitle, newCredits, newSource, newStatus);
    }
    
    /**
     * Creates an updated version of this track with new metadata using ArtistCredit objects.
     * 
     * @param newTitle the new title (may be null to keep current)
     * @param newCredits the new artist credits (may be null to keep current)
     * @param newSource the new source providing this data (must not be null)
     * @param newStatus the new status (may be null to keep current)
     * @return a new Track instance with updated data if source priority allows
     */
    public Track updateWithSourcePriority(String newTitle, List<ArtistCredit> newCredits, Source newSource, TrackStatus newStatus) {
        Objects.requireNonNull(newSource, "new source must not be null");
        
        // Always add the new source if not already present
        List<Source> updatedSources = new ArrayList<>(this.sources);
        if (!updatedSources.contains(newSource)) {
            updatedSources.add(newSource);
        }
        
        // Get current highest priority source
        Source currentHighestPrioritySource = getHighestPrioritySource();
        
        // Only update metadata if new source has higher or equal priority
        ProducerSourcePriority newPriority = ProducerSourcePriority.fromSource(newSource);
        ProducerSourcePriority currentPriority = ProducerSourcePriority.fromSource(currentHighestPrioritySource);
        if (newPriority.hasHigherPriorityThan(currentPriority) || newPriority.equals(currentPriority)) {
            
            String finalTitle = newTitle != null ? validateNonBlank(newTitle, "title") : this.title;
            List<ArtistCredit> finalCredits = newCredits != null ? validateCredits(newCredits) : this.credits;
            TrackStatus finalStatus = newStatus != null ? newStatus : this.status;
            
            return new Track(this.isrc, finalTitle, finalCredits, updatedSources, finalStatus);
        } else {
            // Source priority too low - only update sources list
            return new Track(this.isrc, this.title, this.credits, updatedSources, this.status);
        }
    }

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
                .add("credits=" + credits)
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

    private static List<ArtistCredit> validateCredits(List<ArtistCredit> credits) {
        if (credits == null || credits.isEmpty()) {
            throw new IllegalArgumentException("credits must not be null or empty");
        }
        List<ArtistCredit> validated = new ArrayList<>(credits.size());
        for (ArtistCredit credit : credits) {
            if (credit == null) {
                throw new IllegalArgumentException("credits cannot contain null elements");
            }
            validated.add(credit);
        }
        return validated;
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
