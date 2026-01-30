package com.yowyob.template.domain.ports.out;

import com.yowyob.template.domain.model.Organisation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OrganisationRepositoryPort {
    Mono<Organisation> save(Organisation organisation);
    Mono<Organisation> save(Organisation organisation, String jwtToken);
    Mono<Organisation> findById(UUID id);
    Mono<Organisation> findById(UUID id, String jwtToken);
    Mono<Organisation> findByActorId(UUID actorId);
    Mono<Organisation> findByActorId(UUID actorId, String jwtToken);
    Flux<Organisation> findAll();
    Mono<Void> deleteById(UUID id);
}
