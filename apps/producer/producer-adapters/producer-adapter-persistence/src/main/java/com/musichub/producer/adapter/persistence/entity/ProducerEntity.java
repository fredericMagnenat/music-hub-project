package com.musichub.producer.adapter.persistence.entity;

import com.musichub.producer.domain.values.ProducerId;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "producers")
public class ProducerEntity {

    @Id
    public UUID id;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    public LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    public LocalDateTime updatedAt;


    @Column(name = "producer_code", unique = true, nullable = false, length = 5)
    public String producerCode;

    @Column(name = "name")
    public String name;

    @Column(name = "status", length = 20)
    public String status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "producer_tracks", joinColumns = @JoinColumn(name = "producer_id"))
    @Column(name = "track_isrc", nullable = false, length = 12)
    public Set<String> tracks = new LinkedHashSet<>();

    public ProducerId getProducerId() {
        return new ProducerId(this.id);
    }

    public void setProducerId(ProducerId producerId) {
        this.id = producerId.value();
    }
}
