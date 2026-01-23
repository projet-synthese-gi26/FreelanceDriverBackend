package com.yowyob.template.domain.ports.out;

import java.util.UUID;

import com.yowyob.template.domain.model.BusinessActor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BusinessActorRepositoryPort {

    Mono<BusinessActor> save(BusinessActor businessActor);
    Mono<BusinessActor> save(BusinessActor businessActor, String jwtToken);
    Mono<BusinessActor> findById(UUID id);
    Mono<BusinessActor> findByUserId(UUID userId);
    Mono<BusinessActor> findByUserId(UUID userId, String jwtToken);
    Flux<BusinessActor> findAll();
    Mono<Void> deleteById(UUID id);
    Mono<Void> deleteAll();

}
