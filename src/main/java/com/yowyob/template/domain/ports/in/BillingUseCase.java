package com.yowyob.template.domain.ports.in;

import com.yowyob.template.domain.model.BillingPlan;
import com.yowyob.template.domain.model.BillingSubscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface BillingUseCase {
    Flux<BillingPlan> listActivePlans();

    Mono<BillingSubscription> getOrCreateFreeSubscription(UUID userId);

    Mono<BillingSubscription> getCurrentSubscription(UUID userId);

    Mono<BillingSubscription> subscribe(UUID userId, String planCode);

    Mono<BillingSubscription> cancel(UUID userId);

    Mono<Long> expireDueSubscriptions();
}
