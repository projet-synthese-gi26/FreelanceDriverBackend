package com.yowyob.template.domain.ports.out;

import com.yowyob.template.domain.model.BillingPlan;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface BillingPlanRepositoryPort {
    Flux<BillingPlan> findActive();

    Mono<BillingPlan> findByCode(String code);

    Mono<BillingPlan> findById(UUID id);

    Mono<BillingPlan> save(BillingPlan plan);
}
