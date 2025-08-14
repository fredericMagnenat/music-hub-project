package com.musichub.artist.adapter.persistence;

import com.musichub.artist.domain.model.ArtistStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "artists")
public class ArtistEntity extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(unique = true, nullable = false)
    public String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public ArtistStatus status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "artist_track_references", joinColumns = @JoinColumn(name = "artist_id"))
    @Column(name = "isrc")
    public Set<String> trackReferences;
}
