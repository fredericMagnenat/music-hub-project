package com.musichub.artist.adapter.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import com.musichub.shared.domain.values.SourceType;

import java.util.Objects;

/**
 * JPA embeddable entity for artist sources.
 * Maps the Source value object to database columns.
 */
@Embeddable
public class SourceEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 20)
    public SourceType sourceType;

    @Column(name = "source_id", nullable = false, length = 100)
    public String sourceId;

    public SourceEntity() {
        // JPA requires default constructor
    }

    public SourceEntity(SourceType sourceType, String sourceId) {
        this.sourceType = sourceType;
        this.sourceId = sourceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SourceEntity that = (SourceEntity) o;
        return sourceType == that.sourceType &&
               Objects.equals(sourceId, that.sourceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceType, sourceId);
    }

    @Override
    public String toString() {
        return "SourceEntity{" +
                "sourceType=" + sourceType +
                ", sourceId='" + sourceId + '\'' +
                '}';
    }
}