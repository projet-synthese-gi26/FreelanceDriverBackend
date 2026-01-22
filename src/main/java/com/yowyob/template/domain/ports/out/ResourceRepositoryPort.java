package com.yowyob.template.domain.ports.out;

import com.yowyob.template.domain.model.Resource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ResourceRepositoryPort {
    Mono<Resource> findById(UUID id);
    Flux<Resource> findByOrgId(UUID orgId);
}
