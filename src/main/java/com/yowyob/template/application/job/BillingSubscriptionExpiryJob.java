package com.yowyob.template.application.job;

import com.yowyob.template.domain.ports.in.BillingUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BillingSubscriptionExpiryJob {

    private final BillingUseCase billingUseCase;

    @Scheduled(fixedDelayString = "${application.billing.expiry-job-fixed-delay-ms:600000}")
    public void expireDueSubscriptions() {
        billingUseCase.expireDueSubscriptions()
                .subscribe(count -> {
                    if (count > 0) {
                        log.info("[BILLING] expired subscriptions count={}", count);
                    }
                }, err -> log.error("[BILLING] expiry job failed: {}", err.getMessage()));
    }
}
