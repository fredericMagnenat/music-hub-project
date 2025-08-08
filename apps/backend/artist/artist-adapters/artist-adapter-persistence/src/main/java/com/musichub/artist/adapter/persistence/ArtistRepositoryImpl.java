package com.musichub.artist.adapter.persistence;

import com.musichub.artist.domain.Artist;
import com.musichub.artist.application.port.out.ArtistRepository;
import com.musichub.shared.domain.values.ISRC;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class ArtistRepositoryImpl implements ArtistRepository, PanacheRepository<ArtistEntity> {

    @Override
    public Optional<Artist> findByName(String name) {
        return find("name", name).firstResultOptional().map(ArtistMapper::toDomain);
    }

    @Override
    @Transactional
    public void save(Artist artist) {
        Optional<ArtistEntity> existingEntityOpt = find("name", artist.getName()).firstResultOptional();

        if (existingEntityOpt.isPresent()) {
            // Update existing entity
            ArtistEntity entityToUpdate = existingEntityOpt.get();
            entityToUpdate.trackReferences.addAll(artist.getTrackReferences().stream().map(ISRC::value).collect(Collectors.toSet()));
            // Panache will automatically persist changes on a managed entity within a transaction.
        } else {
            // Create new entity
            ArtistEntity newEntity = ArtistMapper.toDbo(artist);
            persist(newEntity);
        }
    }
}