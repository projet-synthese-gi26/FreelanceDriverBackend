package com.yowyob.template.infrastructure.adapters.inbound.rest;

import com.yowyob.template.domain.model.BillingPlan;
import com.yowyob.template.domain.ports.in.BillingUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/public/billing")
@RequiredArgsConstructor
@Tag(name = "Public Billing", description = "Plans d'abonnement disponibles")
public class PublicBillingController {

    private final BillingUseCase billingUseCase;

    @GetMapping("/plans")
    @Operation(summary = "Lister les plans actifs")
    public Flux<BillingPlan> listPlans() {
        return billingUseCase.listActivePlans();
    }
}
