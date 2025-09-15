package com.musichub.artist.adapter.persistence.entity;

import com.musichub.artist.domain.model.ArtistStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA entity for Artist aggregate.
 * Maps the rich Artist domain model to database tables.
 */
@Entity
@Table(name = "artists")
public class ArtistEntity extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(name = "name", nullable = false, length = 255)
    public String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    public ArtistStatus status;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "artist_contributions",
        joinColumns = @JoinColumn(name = "artist_id")
    )
    public List<ContributionEntity> contributions = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "artist_sources",
        joinColumns = @JoinColumn(name = "artist_id")
    )
    public List<SourceEntity> sources = new ArrayList<>();

    public ArtistEntity() {
        // JPA requires default constructor
    }

    public ArtistEntity(UUID id, String name, ArtistStatus status) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.contributions = new ArrayList<>();
        this.sources = new ArrayList<>();
    }
}
