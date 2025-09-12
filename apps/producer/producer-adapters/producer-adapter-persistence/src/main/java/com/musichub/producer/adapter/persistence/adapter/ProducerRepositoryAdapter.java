package com.musichub.producer.adapter.persistence.adapter;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.musichub.producer.adapter.persistence.entity.ProducerEntity;
import com.musichub.producer.adapter.persistence.exception.ProducerPersistenceException;
import com.musichub.producer.adapter.persistence.mapper.ProducerMapper;
import com.musichub.producer.application.ports.out.ProducerRepository;
import com.musichub.producer.domain.model.Producer;
import com.musichub.producer.domain.values.ProducerId;
import com.musichub.shared.domain.values.ProducerCode;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

/**
 * JPA/Panache implementation of ProducerRepository port.
 * Handles persistence operations for Producer aggregates.
 */
@ApplicationScoped
public class ProducerRepositoryAdapter implements ProducerRepository, PanacheRepository<ProducerEntity> {

    private static final Logger log = LoggerFactory.getLogger(ProducerRepositoryAdapter.class);
    private static final String CORRELATION_ID_KEY = "correlationId";

    @Override
    public Optional<Producer> findByProducerCode(ProducerCode code) {
        String correlationId = MDC.get(CORRELATION_ID_KEY);
        log.debug("Querying database for producer with code: {}, correlationId: {}",
                code.value(), correlationId);

        try {
            Optional<ProducerEntity> entityOpt = find("producerCode", code.value()).firstResultOptional();

            if (entityOpt.isEmpty()) {
                log.debug("No producer found with code: {}", code.value());
                return Optional.empty();
            }

            Producer producer = ProducerMapper.toDomain(entityOpt.get());
            log.debug("Successfully retrieved producer: {}, tracks: {}",
                    code.value(), producer.tracks().size());

            return Optional.of(producer);

        } catch (Exception e) {
            throw new ProducerPersistenceException(
                    String.format("Failed to retrieve producer with code '%s' (correlationId: %s)",
                            code.value(), correlationId),
                    e);
        }
    }

    @Override
    public Optional<Producer> findById(ProducerId id) {
        String correlationId = MDC.get(CORRELATION_ID_KEY);
        log.debug("Querying database for producer with id: {}, correlationId: {}",
                id.value(), correlationId);

        try {
            Optional<ProducerEntity> entityOpt = find("id", id.value()).firstResultOptional();

            if (entityOpt.isEmpty()) {
                log.debug("No producer found with id: {}", id.value());
                return Optional.empty();
            }

            Producer producer = ProducerMapper.toDomain(entityOpt.get());
            log.debug("Successfully retrieved producer by id: {}, producerCode: {}, tracks: {}",
                    id.value(), producer.producerCode().value(), producer.tracks().size());

            return Optional.of(producer);

        } catch (Exception e) {
            throw new ProducerPersistenceException(
                    String.format("Failed to retrieve producer with id '%s' (correlationId: %s)",
                            id.value(), correlationId),
                    e);
        }
    }

    @Override
    @Transactional
    public Producer save(Producer producer) {
        String correlationId = MDC.get(CORRELATION_ID_KEY);
        ProducerId producerId = producer.id();
        ProducerCode producerCode = producer.producerCode();

        log.debug("Saving producer to database - id: {}, code: {}, tracks: {}, correlationId: {}",
                producerId.value(), producerCode.value(), producer.tracks().size(), correlationId);

        try {
            ProducerEntity entity = ProducerMapper.toDbo(producer);
            ProducerEntity persistedEntity = getEntityManager().merge(entity);

            Producer savedProducer = ProducerMapper.toDomain(persistedEntity);

            log.info("Producer saved successfully - id: {}, code: {}, tracks: {}, correlationId: {}",
                    producerId.value(), producerCode.value(), savedProducer.tracks().size(), correlationId);

            return savedProducer;

        } catch (Exception e) {
            throw new ProducerPersistenceException(
                    String.format("Failed to save producer with code '%s' and id '%s' (correlationId: %s)",
                            producerCode.value(), producerId.value(), correlationId),
                    e);
        }
    }

}
