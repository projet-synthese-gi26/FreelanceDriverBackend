package com.yowyob.template.infrastructure.adapters.inbound.rest;

import com.yowyob.template.domain.model.BillingSubscription;
import com.yowyob.template.domain.ports.in.BillingUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/driver/billing")
@RequiredArgsConstructor
@Tag(name = "Driver Billing", description = "Gestion d'abonnement (interne)")
public class DriverBillingController {

    private final BillingUseCase billingUseCase;

    public record SubscribeRequest(@NotBlank String planCode) {}

    @GetMapping("/subscription")
    @Operation(summary = "Récupérer l'abonnement courant")
    public Mono<BillingSubscription> getSubscription(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        UUID userId = extractUserId(principal);
        return billingUseCase.getOrCreateFreeSubscription(userId);
    }

    @PostMapping("/subscribe")
    @Operation(summary = "Souscrire à un plan")
    public Mono<BillingSubscription> subscribe(
            @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
            @Valid @RequestBody SubscribeRequest request
    ) {
        UUID userId = extractUserId(principal);
        return billingUseCase.subscribe(userId, request.planCode());
    }

    @PostMapping("/cancel")
    @Operation(summary = "Annuler en fin de période")
    public Mono<BillingSubscription> cancel(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        UUID userId = extractUserId(principal);
        return billingUseCase.cancel(userId);
    }

    private UUID extractUserId(OAuth2AuthenticatedPrincipal principal) {
        String[] candidates = new String[] {
                principal.getAttribute("sub"),
                principal.getAttribute("user_id"),
                principal.getAttribute("userId"),
                principal.getAttribute("id"),
                principal.getName()
        };

        for (String value : candidates) {
            if (value == null || value.isBlank()) continue;
            try {
                return UUID.fromString(value);
            } catch (IllegalArgumentException ignored) {
            }
        }

        throw new IllegalStateException("Unable to extract user id from OAuth2 principal");
    }
}
