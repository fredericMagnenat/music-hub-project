package com.musichub.artist.adapter.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.UUID;

/**
 * JPA embeddable entity for artist contributions.
 * Maps the Contribution value object to database columns.
 */
@Embeddable
public class ContributionEntity {

    @Column(name = "track_id", nullable = false)
    public UUID trackId;

    @Column(name = "track_title", nullable = false, length = 255)
    public String title;

    @Column(name = "track_isrc", nullable = false, length = 15)
    public String isrc;

    public ContributionEntity() {
        // JPA requires default constructor
    }

    public ContributionEntity(UUID trackId, String title, String isrc) {
        this.trackId = trackId;
        this.title = title;
        this.isrc = isrc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContributionEntity that = (ContributionEntity) o;
        return Objects.equals(trackId, that.trackId) &&
               Objects.equals(title, that.title) &&
               Objects.equals(isrc, that.isrc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trackId, title, isrc);
    }

    @Override
    public String toString() {
        return "ContributionEntity{" +
                "trackId=" + trackId +
                ", title='" + title + '\'' +
                ", isrc='" + isrc + '\'' +
                '}';
    }
}