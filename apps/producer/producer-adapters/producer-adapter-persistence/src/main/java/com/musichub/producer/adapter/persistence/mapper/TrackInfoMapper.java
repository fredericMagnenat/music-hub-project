
package com.musichub.producer.adapter.persistence.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.musichub.producer.adapter.persistence.entity.ArtistCreditEmbeddable;
import com.musichub.producer.adapter.persistence.entity.TrackEntity;
import com.musichub.producer.adapter.persistence.exception.ProducerPersistenceException;
import com.musichub.producer.application.dto.TrackInfo;
import com.musichub.producer.domain.values.TrackStatus;
import com.musichub.shared.domain.values.ISRC;

/**
 * Mapper between TrackEntity persistence model and TrackInfo DTO.
 * Specialized for application layer DTOs with track information only.
 */
public final class TrackInfoMapper {
    
    private static final Logger log = LoggerFactory.getLogger(TrackInfoMapper.class);

    private TrackInfoMapper() {
        // Utility class - prevent instantiation
    }

    /**
     * Converts a TrackEntity persistence object to TrackInfo DTO.
     * Maps only track information without producer details.
     *
     * @param trackEntity the TrackEntity persistence object
     * @return TrackInfo DTO
     * @throws IllegalArgumentException if trackEntity is null
     * @throws ProducerPersistenceException if mapping fails
     */
    public static TrackInfo toDto(TrackEntity trackEntity) {
        if (trackEntity == null) {
            throw new IllegalArgumentException("TrackEntity cannot be null");
        }
        
        try {
            List<String> artistNames = trackEntity.getCredits() != null ?
                    trackEntity.getCredits().stream()
                        .map(ArtistCreditEmbeddable::getArtistName)
                        .collect(Collectors.toList()) :
                    List.of();

            return new TrackInfo(
                ISRC.of(trackEntity.getIsrc()),
                trackEntity.getTitle(),
                artistNames,
                List.copyOf(trackEntity.getSources() != null ? 
                    trackEntity.getSources() : List.of()),
                TrackStatus.valueOf(trackEntity.getStatus()),
                trackEntity.getCreatedAt()
            );
        } catch (Exception e) {
            log.error("Error mapping TrackEntity to TrackInfo for ISRC: {}", trackEntity.getIsrc(), e);
            throw new ProducerPersistenceException(
                "Error mapping track entity with ISRC: " + trackEntity.getIsrc(), e);
        }
    }

    /**
     * Converts a list of TrackEntity objects to TrackInfo DTOs.
     *
     * @param trackEntities list of TrackEntity persistence objects
     * @return list of TrackInfo DTOs
     */
    public static List<TrackInfo> toDtoList(List<TrackEntity> trackEntities) {
        if (trackEntities == null) {
            return List.of();
        }
        
        return trackEntities.stream()
            .map(TrackInfoMapper::toDto)
            .toList();
    }
}
