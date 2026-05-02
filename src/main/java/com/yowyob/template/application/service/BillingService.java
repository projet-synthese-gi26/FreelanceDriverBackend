package com.yowyob.template.application.service;

import com.yowyob.template.domain.model.BillingPlan;
import com.yowyob.template.domain.model.BillingSubscription;
import com.yowyob.template.domain.model.SubscriptionStatus;
import com.yowyob.template.domain.ports.in.BillingUseCase;
import com.yowyob.template.domain.ports.out.BillingPlanRepositoryPort;
import com.yowyob.template.domain.ports.out.BillingSubscriptionRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingService implements BillingUseCase {

    private static final String FREE_PLAN_CODE = "FREE";
    private static final Duration DEFAULT_PERIOD = Duration.ofDays(30);

    private final BillingPlanRepositoryPort planRepository;
    private final BillingSubscriptionRepositoryPort subscriptionRepository;

    @Override
    public Flux<BillingPlan> listActivePlans() {
        return planRepository.findActive();
    }

    @Override
    public Mono<BillingSubscription> getOrCreateFreeSubscription(UUID userId) {
        return subscriptionRepository.findByUserId(userId)
                .switchIfEmpty(Mono.defer(() -> createSubscription(userId, FREE_PLAN_CODE)));
    }

    @Override
    public Mono<BillingSubscription> getCurrentSubscription(UUID userId) {
        return subscriptionRepository.findByUserId(userId);
    }

    @Override
    public Mono<BillingSubscription> subscribe(UUID userId, String planCode) {
        return planRepository.findByCode(planCode)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Unknown plan code: " + planCode)))
                .flatMap(plan -> upsertSubscription(userId, plan.getId()));
    }

    @Override
    public Mono<BillingSubscription> cancel(UUID userId) {
        return subscriptionRepository.findByUserId(userId)
                .switchIfEmpty(Mono.error(new IllegalStateException("Subscription not found")))
                .flatMap(existing -> {
                    existing.setCancelAtPeriodEnd(true);
                    existing.setUpdatedAt(Instant.now());
                    return subscriptionRepository.save(existing);
                });
    }

    @Override
    public Mono<Long> expireDueSubscriptions() {
        Instant now = Instant.now();
        return subscriptionRepository.findActiveWithPeriodEndBefore(now)
                .flatMap(this::expireSubscription)
                .count();
    }

    private Mono<BillingSubscription> createSubscription(UUID userId, String planCode) {
        return planRepository.findByCode(planCode)
                .switchIfEmpty(Mono.error(new IllegalStateException("Missing required plan: " + planCode)))
                .flatMap(plan -> upsertSubscription(userId, plan.getId()));
    }

    private Mono<BillingSubscription> upsertSubscription(UUID userId, UUID planId) {
        Instant now = Instant.now();
        Instant end = now.plus(DEFAULT_PERIOD);

        return subscriptionRepository.findByUserId(userId)
                .flatMap(existing -> {
                    existing.setPlanId(planId);
                    existing.setStatus(SubscriptionStatus.ACTIVE);
                    existing.setCurrentPeriodStart(now);
                    existing.setCurrentPeriodEnd(end);
                    existing.setCancelAtPeriodEnd(false);
                    existing.setUpdatedAt(now);
                    return subscriptionRepository.save(existing);
                })
                .switchIfEmpty(Mono.defer(() -> subscriptionRepository.save(
                        BillingSubscription.builder()
                                .userId(userId)
                                .planId(planId)
                                .status(SubscriptionStatus.ACTIVE)
                                .currentPeriodStart(now)
                                .currentPeriodEnd(end)
                                .cancelAtPeriodEnd(false)
                                .createdAt(now)
                                .updatedAt(now)
                                .build())));
    }

    private Mono<BillingSubscription> expireSubscription(BillingSubscription subscription) {
        Instant now = Instant.now();

        if (Boolean.TRUE.equals(subscription.getCancelAtPeriodEnd())) {
            subscription.setStatus(SubscriptionStatus.CANCELED);
            subscription.setUpdatedAt(now);
            return subscriptionRepository.save(subscription);
        }

        subscription.setStatus(SubscriptionStatus.EXPIRED);
        subscription.setUpdatedAt(now);
        return subscriptionRepository.save(subscription)
                .doOnSuccess(s -> log.info("[BILLING] subscription expired | userId={} | subscriptionId={}", s.getUserId(), s.getId()));
    }
}
