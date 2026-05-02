package com.yowyob.template.infrastructure.adapters.outbound.persistence.repository;

import com.yowyob.template.infrastructure.adapters.outbound.persistence.entity.BillingPlanEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface BillingPlanR2dbcRepository extends ReactiveCrudRepository<BillingPlanEntity, UUID> {

    @Query("SELECT * FROM billing_plans WHERE active = true")
    Flux<BillingPlanEntity> findActive();

    @Query("SELECT * FROM billing_plans WHERE code = :code LIMIT 1")
    Mono<BillingPlanEntity> findByCode(String code);
}
