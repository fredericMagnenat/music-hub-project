package com.musichub.producer.adapter.persistence;

import com.musichub.producer.domain.model.Producer;
import com.musichub.producer.domain.ports.out.ProducerRepository;
import com.musichub.shared.domain.values.ProducerCode;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.Optional;

@ApplicationScoped
public class ProducerRepositoryImpl implements ProducerRepository, PanacheRepository<ProducerEntity> {

    @Override
    public Optional<Producer> findByProducerCode(ProducerCode code) {
        return find("producerCode", code.value()).firstResultOptional().map(ProducerMapper::toDomainWithTracks);
    }

    @Override
    public Optional<Producer> findById(com.musichub.producer.domain.values.ProducerId id) {
        return find("id", id.value()).firstResultOptional().map(ProducerMapper::toDomainWithTracks);
    }

    @Override
    @Transactional
    public Producer save(Producer producer) {
        Optional<ProducerEntity> existing = find("producerCode", producer.producerCode().value()).firstResultOptional();
        if (existing.isPresent()) {
            ProducerEntity entity = existing.get();
            entity.name = producer.name();
            entity.tracks = producer.tracks().stream().map(com.musichub.shared.domain.values.ISRC::value).collect(java.util.stream.Collectors.toSet());
            return ProducerMapper.toDomainWithTracks(entity);
        } else {
            ProducerEntity entity = ProducerMapper.toDbo(producer);
            persist(entity);
            return ProducerMapper.toDomainWithTracks(entity);
        }
    }
}
