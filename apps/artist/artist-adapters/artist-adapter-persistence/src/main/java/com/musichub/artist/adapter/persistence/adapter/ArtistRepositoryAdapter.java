package com.musichub.artist.adapter.persistence.adapter;

import com.musichub.artist.adapter.persistence.entity.ArtistEntity;
import com.musichub.artist.adapter.persistence.entity.ContributionEntity;
import com.musichub.artist.adapter.persistence.entity.SourceEntity;
import com.musichub.artist.adapter.persistence.exception.ArtistPersistenceException;
import com.musichub.artist.adapter.persistence.mapper.ArtistMapper;
import com.musichub.artist.application.ports.out.ArtistRepository;
import com.musichub.artist.domain.model.Artist;
import com.musichub.shared.domain.id.ArtistId;
import com.musichub.shared.domain.values.SourceType;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA implementation of ArtistRepository.
 * Handles persistence of complete Artist aggregates including contributions and sources.
 */
@ApplicationScoped
public class ArtistRepositoryAdapter implements ArtistRepository, PanacheRepositoryBase<ArtistEntity, UUID> {

    private static final Logger log = LoggerFactory.getLogger(ArtistRepositoryAdapter.class);
    private static final String CORRELATION_ID_KEY = "correlationId";

    @Inject
    private ArtistMapper artistMapper;


    @Override
    public Optional<Artist> findByName(String name) {
        String correlationId = MDC.get(CORRELATION_ID_KEY);
        log.debug("Querying database for artist with name: {}, correlationId: {}", name, correlationId);

        try {
            Optional<ArtistEntity> entityOpt = find("name", name).firstResultOptional();

            if (entityOpt.isEmpty()) {
                log.debug("No artist found with name: {}", name);
                return Optional.empty();
            }

            Artist artist = artistMapper.toDomain(entityOpt.get());
            log.debug("Successfully retrieved artist by name: {}, id: {}, contributions: {}", 
                    name, artist.getId().value(), artist.getContributions().size());

            return Optional.of(artist);

        } catch (Exception e) {
            throw new ArtistPersistenceException(
                    String.format("Failed to retrieve artist with name '%s' (correlationId: %s)", 
                            name, correlationId), e);
        }
    }

    @Override
    public Optional<Artist> findById(ArtistId id) {
        String correlationId = MDC.get(CORRELATION_ID_KEY);
        log.debug("Querying database for artist with id: {}, correlationId: {}", 
                id.value(), correlationId);

        try {
            Optional<ArtistEntity> entityOpt = findByIdOptional(id.value());

            if (entityOpt.isEmpty()) {
                log.debug("No artist found with id: {}", id.value());
                return Optional.empty();
            }

            Artist artist = artistMapper.toDomain(entityOpt.get());
            log.debug("Successfully retrieved artist by id: {}, name: {}, contributions: {}", 
                    id.value(), artist.getNameValue(), artist.getContributions().size());

            return Optional.of(artist);

        } catch (Exception e) {
            throw new ArtistPersistenceException(
                    String.format("Failed to retrieve artist with id '%s' (correlationId: %s)", 
                            id.value(), correlationId), e);
        }
    }

    @Override
    public Optional<Artist> findBySource(SourceType sourceType, String sourceId) {
        String correlationId = MDC.get(CORRELATION_ID_KEY);
        log.debug("Querying database for artist with source type: {}, sourceId: {}, correlationId: {}", 
                sourceType, sourceId, correlationId);

        try {
            Optional<ArtistEntity> entityOpt = find("SELECT DISTINCT a FROM ArtistEntity a JOIN a.sources s WHERE s.sourceType = ?1 AND s.sourceId = ?2",
                    sourceType, sourceId).firstResultOptional();

            if (entityOpt.isEmpty()) {
                log.debug("No artist found with source type: {}, sourceId: {}", sourceType, sourceId);
                return Optional.empty();
            }

            Artist artist = artistMapper.toDomain(entityOpt.get());
            log.debug("Successfully retrieved artist by source - type: {}, sourceId: {}, artistId: {}, name: {}", 
                    sourceType, sourceId, artist.getId().value(), artist.getNameValue());

            return Optional.of(artist);

        } catch (Exception e) {
            throw new ArtistPersistenceException(
                    String.format("Failed to retrieve artist with source type '%s' and sourceId '%s' (correlationId: %s)", 
                            sourceType, sourceId, correlationId), e);
        }
    }

    @Override
    @Transactional
    public Artist save(Artist artist) {
        String correlationId = MDC.get(CORRELATION_ID_KEY);
        ArtistId artistId = artist.getId();
        String artistName = artist.getNameValue();

        log.debug("Saving artist to database - id: {}, name: {}, contributions: {}, sources: {}, correlationId: {}", 
                artistId.value(), artistName, artist.getContributions().size(), 
                artist.getSources().size(), correlationId);

        try {
            Optional<ArtistEntity> existingEntityOpt = findByIdOptional(artist.getId().value());

            ArtistEntity entityToSave;
            if (existingEntityOpt.isPresent()) {
                entityToSave = existingEntityOpt.get();
                updateEntityFromDomain(entityToSave, artist);
                log.debug("Updated existing artist entity - id: {}, name: {}", 
                        artistId.value(), artistName);
            } else {
                entityToSave = artistMapper.toDbo(artist);
                persist(entityToSave);
                log.debug("Created new artist entity - id: {}, name: {}", 
                        artistId.value(), artistName);
            }

            Artist savedArtist = artistMapper.toDomain(entityToSave);

            log.info("Artist saved successfully - id: {}, name: {}, contributions: {}, sources: {}, correlationId: {}", 
                    artistId.value(), artistName, savedArtist.getContributions().size(), 
                    savedArtist.getSources().size(), correlationId);

            return savedArtist;

        } catch (Exception e) {
            throw new ArtistPersistenceException(
                    String.format("Failed to save artist with id '%s' and name '%s' (correlationId: %s)", 
                            artistId.value(), artistName, correlationId), e);
        }
    }

    /**
     * Updates an existing JPA entity with data from the domain model.
     * Handles collections properly to avoid JPA issues.
     */
    private void updateEntityFromDomain(ArtistEntity entity, Artist domain) {
        entity.name = domain.getNameValue();
        entity.status = domain.getStatus();

        // Clear and repopulate contributions
        entity.contributions.clear();
        domain.getContributions().forEach(contribution -> 
            entity.contributions.add(new ContributionEntity(
                contribution.trackId().value(),
                contribution.title(),
                contribution.isrc().value()
            ))
        );

        // Clear and repopulate sources
        entity.sources.clear();
        domain.getSources().forEach(source -> 
            entity.sources.add(new SourceEntity(
                source.sourceType(),
                source.sourceId()
            ))
        );
    }
}