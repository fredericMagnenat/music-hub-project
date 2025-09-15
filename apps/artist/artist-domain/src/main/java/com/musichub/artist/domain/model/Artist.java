package com.musichub.artist.domain.model;

import com.musichub.artist.domain.values.ArtistSourcePriorityProvider;
import com.musichub.shared.domain.id.ArtistId;
import com.musichub.artist.domain.values.ArtistName;
import com.musichub.artist.domain.values.Contribution;
import com.musichub.shared.domain.values.Source;
import com.musichub.shared.domain.values.SourcePriorityProvider;
import com.musichub.shared.domain.values.SourceType;

import java.util.*;

/**
 * Artist aggregate root representing an artist entity with its contributions and sources.
 * Immutable aggregate that enforces business rules and status transitions.
 * Based on domain charter specification.
 * 
 * Business Rules:
 * - Artists can exist on multiple platforms simultaneously (multi-platform coexistence)
 * - Only one source per platform type is allowed (duplicates are replaced)
 * - Only PROVISIONAL artists can be marked as VERIFIED
 * - Contributions are unique per artist (idempotent additions)
 */
public class Artist {

    private final ArtistId id;
    private final ArtistName name;
    private final ArtistStatus status;
    private final List<Contribution> contributions;
    private final List<Source> sources;

    // ===========================
    // CONSTANTS
    // ===========================
    
    private static final String ERROR_ARTIST_NAME_NULL = "Artist name cannot be null";
    private static final String ERROR_SOURCE_TYPE_NULL = "Source type cannot be null";
    
    /**
     * Source priority hierarchy as defined in domain charter.
     * Higher priority sources are preferred when conflicts occur.
     * MANUAL > TIDAL > SPOTIFY > DEEZER > APPLE_MUSIC
     */
    private static final SourcePriorityProvider PRIORITY_PROVIDER =
            new ArtistSourcePriorityProvider();

    // ===========================
    // CONSTRUCTOR
    // ===========================
    
    private Artist(ArtistId id, ArtistName name, ArtistStatus status,
                   List<Contribution> contributions, List<Source> sources) {
        this.id = Objects.requireNonNull(id, "Artist ID cannot be null");
        this.name = Objects.requireNonNull(name, ERROR_ARTIST_NAME_NULL);
        this.status = Objects.requireNonNull(status, "Artist status cannot be null");
        this.contributions = new ArrayList<>(contributions != null ? contributions : Collections.emptyList());
        this.sources = new ArrayList<>(sources != null ? sources : Collections.emptyList());
    }

    // ===========================
    // FACTORY METHODS
    // ===========================

    /**
     * Creates a provisional artist with the given name.
     * Provisional artists need to be verified through external sources.
     *
     * @param name the artist name
     * @return a new provisional Artist
     * @throws NullPointerException if name is null
     */
    public static Artist createProvisional(String name) {
        Objects.requireNonNull(name, ERROR_ARTIST_NAME_NULL);
        return new Artist(
                new ArtistId(UUID.randomUUID()),
                ArtistName.of(name),
                ArtistStatus.PROVISIONAL,
                Collections.emptyList(),
                Collections.emptyList()
        );
    }

    // ===========================
    // FACTORY METHODS
    // ===========================

    /**
     * Creates an artist from existing data (for reconstruction from persistence).
     *
     * @param id            the artist ID
     * @param name          the artist name
     * @param status        the artist status
     * @param contributions the list of contributions
     * @param sources       the list of sources
     * @return a new Artist instance
     * @throws NullPointerException if any required parameter is null
     */
    public static Artist from(ArtistId id, ArtistName name, ArtistStatus status,
                              List<Contribution> contributions, List<Source> sources) {
        return new Artist(id, name, status, contributions, sources);
    }

    /**
     * Creates an artist from basic parameters (backward compatibility).
     *
     * @param id         the artist ID
     * @param nameString the artist name as string
     * @param status     the artist status
     * @return a new Artist instance
     * @throws NullPointerException if any required parameter is null
     */
    public static Artist from(ArtistId id, String nameString, ArtistStatus status) {
        return new Artist(id, ArtistName.of(nameString), status,
                Collections.emptyList(), Collections.emptyList());
    }

    // ===========================
    // BUSINESS METHODS
    // ===========================

    /**
     * Adds a contribution to this artist.
     * Returns a new Artist instance with the added contribution.
     * Operation is idempotent - adding the same contribution twice returns the same instance.
     *
     * @param contribution the contribution to add
     * @return a new Artist instance with the contribution, or same instance if contribution already exists
     * @throws NullPointerException if contribution is null
     */
    public Artist addContribution(Contribution contribution) {
        Objects.requireNonNull(contribution, "Contribution cannot be null");

        // Business rule: Operation is idempotent
        if (this.contributions.contains(contribution)) {
            return this;
        }

        List<Contribution> newContributions = new ArrayList<>(this.contributions);
        newContributions.add(contribution);

        return new Artist(this.id, this.name, this.status, newContributions, this.sources);
    }

    /**
     * Adds a source to this artist implementing multi-platform coexistence rule.
     * Replaces existing source of the same type to avoid duplicates.
     * Different source types coexist (business rule: multi-platform presence).
     * <p>
     * Business Rule: An artist can have sources from multiple platforms simultaneously,
     * but only one source per platform type.
     *
     * @param source the source to add
     * @return a new Artist instance with the updated sources
     * @throws NullPointerException if source is null
     */
    public Artist addSource(Source source) {
        Objects.requireNonNull(source, "Source cannot be null");

        List<Source> newSources = new ArrayList<>(this.sources);

        // Remove existing source of the same type (to avoid duplicates)
        newSources.removeIf(existingSource ->
                existingSource.sourceType().equals(source.sourceType())
        );

        newSources.add(source);

        return new Artist(this.id, this.name, this.status, this.contributions, newSources);
    }

    /**
     * Updates the artist name if the new name comes from a higher priority source.
     * Used during enrichment process when external sources provide artist names.
     *
     * @param newName    the new artist name
     * @param sourceType the source type providing the new name
     * @return a new Artist instance with updated name if source has higher priority, same instance otherwise
     */
    public Artist updateNameFromSource(ArtistName newName, SourceType sourceType) {
        Objects.requireNonNull(newName, ERROR_ARTIST_NAME_NULL);
        Objects.requireNonNull(sourceType, ERROR_SOURCE_TYPE_NULL);

        // Find the highest priority source we currently have
        SourceType currentHighestPrioritySource = getCurrentHighestPrioritySourceType();

        // Update name if new source has higher OR EQUAL priority, or no sources exist
        if (currentHighestPrioritySource == null ||
                PRIORITY_PROVIDER.hasHigherPriority(sourceType, currentHighestPrioritySource) ||
                sourceType.equals(currentHighestPrioritySource)) {
            return new Artist(this.id, newName, this.status, this.contributions, this.sources);
        }

        return this; // No change if source has lower priority
    }

    /**
     * Gets the source type with highest priority from current sources.
     */
    private SourceType getCurrentHighestPrioritySourceType() {
        if (sources.isEmpty()) {
            return null;
        }

        SourceType highest = sources.get(0).sourceType();
        for (Source source : sources) {
            if (PRIORITY_PROVIDER.hasHigherPriority(source.sourceType(), highest)) {
                highest = source.sourceType();
            }
        }
        return highest;
    }

    /**
     * Checks if this artist has a source of the given type.
     * Optimized for performance with direct iteration instead of streams.
     *
     * @param sourceType the source type to check
     * @return true if source exists, false otherwise
     * @throws NullPointerException if sourceType is null
     */
    public boolean hasSource(SourceType sourceType) {
        Objects.requireNonNull(sourceType, ERROR_SOURCE_TYPE_NULL);

        // ✅ Performance optimization: direct iteration instead of stream
        for (Source source : sources) {
            if (source.sourceType().equals(sourceType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the source of the given type if it exists.
     * Optimized for performance with direct iteration instead of streams.
     *
     * @param sourceType the source type
     * @return Optional containing the source, or empty if not found
     * @throws NullPointerException if sourceType is null
     */
    public Optional<Source> getSource(SourceType sourceType) {
        Objects.requireNonNull(sourceType, ERROR_SOURCE_TYPE_NULL);

        // ✅ Performance optimization: direct iteration instead of stream
        for (Source source : sources) {
            if (source.sourceType().equals(sourceType)) {
                return Optional.of(source);
            }
        }
        return Optional.empty();
    }

    /**
     * Marks this artist as verified.
     * Only provisional artists can be marked as verified (business rule).
     *
     * @return a new Artist instance with VERIFIED status
     * @throws IllegalStateException if artist is not provisional
     */
    public Artist markAsVerified() {
        if (this.status != ArtistStatus.PROVISIONAL) {
            throw new IllegalStateException("Only provisional artists can be marked as verified");
        }

        return new Artist(this.id, this.name, ArtistStatus.VERIFIED, this.contributions, this.sources);
    }

    // ===========================
    // GETTERS
    // ===========================

    public ArtistId getId() {
        return id;
    }

    public ArtistName getName() {
        return name;
    }

    /**
     * Convenience method to get the artist name as String.
     *
     * @return the artist name value
     */
    public String getNameValue() {
        return name.value();
    }

    public ArtistStatus getStatus() {
        return status;
    }

    /**
     * Gets an unmodifiable view of the artist's contributions.
     *
     * @return unmodifiable list of contributions
     */
    public List<Contribution> getContributions() {
        return Collections.unmodifiableList(contributions);
    }

    /**
     * Gets an unmodifiable view of the artist's sources.
     *
     * @return unmodifiable list of sources
     */
    public List<Source> getSources() {
        return Collections.unmodifiableList(sources);
    }

    // ===========================
    // OBJECT METHODS
    // ===========================

    /**
     * Artists are equal if they have the same ID (business identity).
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artist artist = (Artist) o;
        return id.equals(artist.id);
    }

    /**
     * Hash code based on business identity (ID).
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * String representation showing key information without exposing internal collections.
     */
    @Override
    public String toString() {
        return "Artist{" +
                "id=" + id +
                ", name=" + name +
                ", status=" + status +
                ", contributions=" + contributions.size() +
                ", sources=" + sources.size() +
                '}';
    }

}
