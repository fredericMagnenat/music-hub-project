package com.musichub.producer.adapter.persistence.entity;

import com.musichub.producer.domain.values.ProducerId;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "producers")
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
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
    @EqualsAndHashCode.Include
    public String producerCode;

    @Column(name = "name")
    public String name;

    @Column(name = "status", length = 20)
    public String status;

    @OneToMany(mappedBy = "producer",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    public Set<TrackEntity> tracks = new LinkedHashSet<>();

    public ProducerId getProducerId() {
        return new ProducerId(this.id);
    }

    public void setProducerId(ProducerId producerId) {
        this.id = producerId.value();
    }
}
