package com.yowyob.template.domain.ports.out;

import com.yowyob.template.domain.model.Vehicle;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface VehicleRepositoryPort {
    Mono<Vehicle> findById(UUID id);
    Flux<Vehicle> findByOrgId(UUID orgId);
}
