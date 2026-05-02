package com.yowyob.template.infrastructure.adapters.outbound.persistence.repository;

import com.yowyob.template.infrastructure.adapters.outbound.persistence.entity.BillingSubscriptionEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface BillingSubscriptionR2dbcRepository extends ReactiveCrudRepository<BillingSubscriptionEntity, UUID> {

    @Query("SELECT * FROM billing_subscriptions WHERE user_id = :userId LIMIT 1")
    Mono<BillingSubscriptionEntity> findByUserId(UUID userId);

    @Query("SELECT * FROM billing_subscriptions WHERE status = 'ACTIVE' AND current_period_end < :instant")
    Flux<BillingSubscriptionEntity> findActiveWithPeriodEndBefore(Instant instant);
}
