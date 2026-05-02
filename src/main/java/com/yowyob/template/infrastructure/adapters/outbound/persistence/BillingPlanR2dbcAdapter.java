package com.yowyob.template.infrastructure.adapters.outbound.persistence;

import com.yowyob.template.domain.model.BillingPlan;
import com.yowyob.template.domain.ports.out.BillingPlanRepositoryPort;
import com.yowyob.template.infrastructure.adapters.outbound.persistence.repository.BillingPlanR2dbcRepository;
import com.yowyob.template.infrastructure.mappers.BillingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BillingPlanR2dbcAdapter implements BillingPlanRepositoryPort {

    private final BillingPlanR2dbcRepository repository;
    private final BillingMapper mapper;

    @Override
    public Flux<BillingPlan> findActive() {
        return repository.findActive().map(mapper::toDomain);
    }

    @Override
    public Mono<BillingPlan> findByCode(String code) {
        return repository.findByCode(code).map(mapper::toDomain);
    }

    @Override
    public Mono<BillingPlan> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Mono<BillingPlan> save(BillingPlan plan) {
        return repository.save(mapper.toEntity(plan)).map(mapper::toDomain);
    }
}
