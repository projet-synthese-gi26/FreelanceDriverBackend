package com.yowyob.template.domain.ports.in;

import com.yowyob.template.domain.model.BusinessActor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface createBusinessActorUseCase {
    Mono<BusinessActor> createBusinessActor(BusinessActor businessActor);
    Mono<BusinessActor> getBusinessActorById(UUID id);
    Flux<BusinessActor> getAllBusinessActors();
    Mono<BusinessActor> updateBusinessActor(UUID id, BusinessActor businessActor);
    Mono<Void> deleteBusinessActor(UUID id);
}
