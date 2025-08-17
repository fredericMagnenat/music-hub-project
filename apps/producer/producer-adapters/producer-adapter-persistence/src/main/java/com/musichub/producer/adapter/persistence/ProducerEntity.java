package com.musichub.producer.adapter.persistence;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "producers")
public class ProducerEntity extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(name = "producer_code", unique = true, nullable = false, length = 5)
    public String producerCode;

    @Column(name = "name")
    public String name;

    @Column(name = "status", length = 20)
    public String status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "producer_tracks", joinColumns = @JoinColumn(name = "producer_id"))
    @Column(name = "isrc", nullable = false, length = 12)
    public Set<String> tracks;
}
