package com.musichub.producer.adapter.persistence.adapter;

import com.musichub.producer.adapter.persistence.entity.ProducerEntity;
import com.musichub.producer.adapter.persistence.mapper.ProducerMapper;
import com.musichub.producer.domain.model.Producer;
import com.musichub.producer.domain.ports.out.ProducerRepository;
import com.musichub.producer.domain.values.ProducerId;
import com.musichub.shared.domain.values.ProducerCode;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.Optional;

/**
 * JPA/Panache implementation of ProducerRepository port.
 * Handles persistence operations for Producer aggregates.
 */
@ApplicationScoped
public class ProducerRepositoryAdapter implements ProducerRepository, PanacheRepository<ProducerEntity> {

    @Override
    public Optional<Producer> findByProducerCode(ProducerCode code) {
        return find("producerCode", code.value()).firstResultOptional().map(ProducerMapper::toDomain);
    }

    @Override
    public Optional<Producer> findById(ProducerId id) {
        return find("id", id.value()).firstResultOptional().map(ProducerMapper::toDomain);
    }

    @Override
    @Transactional
    public Producer save(Producer producer) {
        Optional<ProducerEntity> existing = find("producerCode", producer.producerCode().value()).firstResultOptional();
        if (existing.isPresent()) {
            ProducerEntity entity = existing.get();
            entity.name = producer.name();
            entity.tracks = producer.tracks().stream().map(track -> track.isrc().value()).collect(java.util.stream.Collectors.toCollection(java.util.LinkedHashSet::new));
            return ProducerMapper.toDomain(entity);
        } else {
            ProducerEntity entity = ProducerMapper.toDbo(producer);
            persist(entity);
            return ProducerMapper.toDomain(entity);
        }
    }
}
