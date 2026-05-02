package com.yowyob.template.application.bootstrap;

import com.yowyob.template.domain.model.BillingPlan;
import com.yowyob.template.domain.ports.out.BillingPlanRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class BillingBootstrap implements ApplicationRunner {

    private final BillingPlanRepositoryPort planRepository;

    @Override
    public void run(ApplicationArguments args) {
        ensurePlan("FREE", "Free", BigDecimal.ZERO)
                .then(ensurePlan("PRO", "Pro", new BigDecimal("5000")))
                .then(ensurePlan("PREMIUM", "Premium", new BigDecimal("10000")))
                .subscribe(
                        unused -> { },
                        err -> log.error("[BILLING] bootstrap failed: {}", err.getMessage()),
                        () -> log.info("[BILLING] bootstrap done")
                );
    }

    private Mono<BillingPlan> ensurePlan(String code, String name, BigDecimal price) {
        Instant now = Instant.now();
        return planRepository.findByCode(code)
                .flatMap(existing -> {
                    existing.setName(name);
                    existing.setPrice(price);
                    existing.setCurrency("XAF");
                    existing.setPeriod("MONTHLY");
                    existing.setActive(true);
                    existing.setUpdatedAt(now);
                    if (existing.getCreatedAt() == null) {
                        existing.setCreatedAt(now);
                    }
                    return planRepository.save(existing);
                })
                .switchIfEmpty(Mono.defer(() -> planRepository.save(BillingPlan.builder()
                        .code(code)
                        .name(name)
                        .price(price)
                        .currency("XAF")
                        .period("MONTHLY")
                        .active(true)
                        .createdAt(now)
                        .updatedAt(now)
                        .build())))
                .doOnNext(p -> log.info("[BILLING] plan ready | code={} | id={} | price={}", p.getCode(), p.getId(), p.getPrice()));
    }
}
