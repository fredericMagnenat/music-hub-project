package com.musichub.producer.adapter.persistence.adapter;

import com.musichub.producer.adapter.persistence.entity.TrackEntity;
import com.musichub.producer.adapter.persistence.exception.ProducerPersistenceException;
import com.musichub.producer.adapter.persistence.mapper.TrackInfoMapper;
import com.musichub.producer.application.dto.TrackInfo;
import com.musichub.producer.application.ports.out.TrackRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.List;

@ApplicationScoped
public class TrackRepositoryAdapter implements TrackRepository, PanacheRepository<TrackEntity> {
    
    private static final Logger log = LoggerFactory.getLogger(TrackRepositoryAdapter.class);
    private static final String CORRELATION_ID_KEY = "correlationId";
    private static final String CREATED_AT_FIELD = "createdAt";
    private static final int MAX_TRACKS_LIMIT = 1000;
    
    @Override
    public List<TrackInfo> findRecentTracks(int limit) {
        // Validation des paramètres
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive, got: " + limit);
        }
        if (limit > MAX_TRACKS_LIMIT) {
            log.warn("Large limit requested: {}, capping to {}", limit, MAX_TRACKS_LIMIT);
            limit = MAX_TRACKS_LIMIT;
        }
        
        String correlationId = MDC.get(CORRELATION_ID_KEY);
        log.debug("Querying database for {} most recent tracks, correlationId: {}", limit, correlationId);
        
        long startTime = System.currentTimeMillis();
        try {
            log.debug("Executing findAll query with limit: {}", limit);
            
            // ✅ Version Panache simple et élégante !
            List<TrackEntity> trackEntities = findAll(Sort.descending(CREATED_AT_FIELD))
                .page(0, limit)
                .list();
            
            log.debug("Query returned {} TrackEntity objects", trackEntities.size());
            
            List<TrackInfo> trackInfos = TrackInfoMapper.toDtoList(trackEntities);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("Successfully retrieved {} recent tracks from database in {}ms", 
                    trackInfos.size(), duration);
            
            return trackInfos;
            
        } catch (PersistenceException e) {
            throw new ProducerPersistenceException(
                String.format("Database error while retrieving recent tracks (limit: %d, correlationId: %s)", 
                    limit, correlationId), e);
        } catch (Exception e) {
            throw new ProducerPersistenceException(
                String.format("Unexpected error while retrieving recent tracks (limit: %d, correlationId: %s)", 
                    limit, correlationId), e);
        }
    }
}
