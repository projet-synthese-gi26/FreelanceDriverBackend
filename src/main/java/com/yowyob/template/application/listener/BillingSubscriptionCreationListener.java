package com.yowyob.template.application.listener;

import com.yowyob.template.domain.event.DriverOnboardedEvent;
import com.yowyob.template.domain.ports.in.BillingUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BillingSubscriptionCreationListener {

    private final BillingUseCase billingUseCase;

    @Async
    @EventListener
    public void handleDriverOnboarding(DriverOnboardedEvent event) {
        if (!"DRIVER".equalsIgnoreCase(event.role())) {
            return;
        }

        String eventId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        billingUseCase.getOrCreateFreeSubscription(event.userId())
                .subscribe(
                        sub -> log.info("[BILLING] [ID:{}] FREE subscription ready | userId={} | subscriptionId={} | planId={} | end={}",
                                eventId, sub.getUserId(), sub.getId(), sub.getPlanId(), sub.getCurrentPeriodEnd()),
                        err -> log.error("[BILLING] [ID:{}] failed to init subscription | userId={} | error={}",
                                eventId, event.userId(), err.getMessage())
                );
    }
}
