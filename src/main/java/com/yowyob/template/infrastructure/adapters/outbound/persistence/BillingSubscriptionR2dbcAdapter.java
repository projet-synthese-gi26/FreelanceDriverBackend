package com.yowyob.template.infrastructure.adapters.outbound.persistence;

import com.yowyob.template.domain.model.BillingSubscription;
import com.yowyob.template.domain.ports.out.BillingSubscriptionRepositoryPort;
import com.yowyob.template.infrastructure.adapters.outbound.persistence.repository.BillingSubscriptionR2dbcRepository;
import com.yowyob.template.infrastructure.mappers.BillingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BillingSubscriptionR2dbcAdapter implements BillingSubscriptionRepositoryPort {

    private final BillingSubscriptionR2dbcRepository repository;
    private final BillingMapper mapper;

    @Override
    public Mono<BillingSubscription> findByUserId(UUID userId) {
        return repository.findByUserId(userId).map(mapper::toDomain);
    }

    @Override
    public Mono<BillingSubscription> save(BillingSubscription subscription) {
        return repository.save(mapper.toEntity(subscription)).map(mapper::toDomain);
    }

    @Override
    public Flux<BillingSubscription> findActiveWithPeriodEndBefore(Instant instant) {
        return repository.findActiveWithPeriodEndBefore(instant).map(mapper::toDomain);
    }
}
