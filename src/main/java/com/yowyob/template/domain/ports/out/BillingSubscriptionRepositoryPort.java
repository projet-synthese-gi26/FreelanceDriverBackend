package com.yowyob.template.domain.ports.out;

import com.yowyob.template.domain.model.BillingSubscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

public interface BillingSubscriptionRepositoryPort {
    Mono<BillingSubscription> findByUserId(UUID userId);

    Mono<BillingSubscription> save(BillingSubscription subscription);

    Flux<BillingSubscription> findActiveWithPeriodEndBefore(Instant instant);
}
