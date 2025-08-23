package com.musichub.producer.adapter.persistence.entity;

import com.musichub.shared.domain.id.TrackId;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tracks")
public class TrackEntity {

    @Id
    public UUID id;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    public LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    public LocalDateTime updatedAt;

    @Column(name = "isrc", nullable = false, unique = true, length = 12)
    public String isrc;

    @Column(name = "title")
    public String title;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "track_artists", joinColumns = @JoinColumn(name = "track_id"))
    @Column(name = "artist_name", nullable = false)
    public List<String> artistNames;

    @Column(name = "sources", columnDefinition = "jsonb")
    public String sourcesJson;

    @Column(name = "status", length = 20)
    public String status;

    public TrackId getTrackId() {
        return new TrackId(this.id);
    }

    public void setTrackId(TrackId trackId) {
        this.id = trackId.value();
    }
}
