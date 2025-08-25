package com.musichub.producer.adapter.persistence.entity;

import com.musichub.producer.domain.values.Source;
import com.musichub.shared.domain.id.TrackId;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tracks")
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TrackEntity {

    @Id
    private UUID id;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "isrc", nullable = false, unique = true, length = 12)
    @EqualsAndHashCode.Include
    private String isrc;

    @Column(name = "title")
    private String title;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "track_artists", joinColumns = @JoinColumn(name = "track_id"))
    @Column(name = "artist_name", nullable = false)
    private List<String> artistNames;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sources")
    private List<Source> sources;

    @Column(name = "status", length = 20)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producer_id")
    private ProducerEntity producer;

    // TrackId helper methods
    public TrackId getTrackId() {
        return new TrackId(this.id);
    }

    public void setTrackId(TrackId trackId) {
        this.id = trackId.value();
    }
}
