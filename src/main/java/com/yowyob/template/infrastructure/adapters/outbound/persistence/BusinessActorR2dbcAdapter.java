package com.yowyob.template.infrastructure.adapters.outbound.persistence;

import com.yowyob.template.domain.model.BusinessActor;
import com.yowyob.template.domain.ports.out.BusinessActorRepositoryPort;
import com.yowyob.template.infrastructure.adapters.outbound.persistence.repository.BusinessActorR2dbcRepository;
import com.yowyob.template.infrastructure.mappers.BusinessActorMapper;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BusinessActorR2dbcAdapter implements BusinessActorRepositoryPort {

    private final BusinessActorR2dbcRepository repository;
    private final BusinessActorMapper mapper;

    @Override
    public Mono<BusinessActor> save(BusinessActor businessActor) {
        return repository.save(mapper.toEntity(businessActor))
                .map(mapper::toDomain);
    }
   

    @Override
    public Flux<BusinessActor> findAll() {
        return repository.findAll()
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteAll() {
        return repository.deleteAll();
    }

    @Override
    public Mono<BusinessActor> findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id);
    }
}