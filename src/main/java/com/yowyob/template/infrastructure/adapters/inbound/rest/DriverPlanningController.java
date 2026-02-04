package com.yowyob.template.infrastructure.adapters.inbound.rest;

import com.yowyob.template.application.service.DriverPlanningService;
import com.yowyob.template.domain.model.Product;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.request.CreatePlanningRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.request.UpdatePlanningRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/driver/plannings")
@RequiredArgsConstructor
@Tag(name = "Driver Planning", description = "Gestion des plannings (Produits) pour les Chauffeurs")
public class DriverPlanningController {
    
    private final DriverPlanningService planningService;
    // Garde le ProductService pour les opérations génériques GET/DELETE si besoin
    private final com.yowyob.template.application.service.ProductService productService;

    @PostMapping
    @Operation(summary = "Créer un planning", description = "Crée un nouveau créneau de transport pour le chauffeur connecté.")
    public Mono<Product> createPlanning(
            @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
            @Parameter(hidden = true) @RequestHeader(name = "Authorization") String token,
            @Valid @RequestBody CreatePlanningRequest request
    ) {
        UUID authUserId = UUID.fromString(principal.getAttribute("sub"));
        return planningService.createDriverPlanning(authUserId, request, token);
    }

    @GetMapping
    public Flux<Product> listPlannings(
            @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
            @Parameter(hidden = true) @RequestHeader(name = "Authorization") String token
    ) {
        UUID authUserId = UUID.fromString(principal.getAttribute("sub"));
        return planningService.listDriverPlannings(authUserId, token);
    }

    @GetMapping("/{id}")
    public Mono<Product> getPlanning(
            @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
            @Parameter(hidden = true) @RequestHeader(name = "Authorization") String token,
            @PathVariable UUID id
    ) {
        UUID authUserId = UUID.fromString(principal.getAttribute("sub"));
        return planningService.getDriverPlanning(authUserId, id, token);
    }

    @PutMapping("/{id}")
    public Mono<Product> updatePlanning(
            @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
            @Parameter(hidden = true) @RequestHeader(name = "Authorization") String token,
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePlanningRequest request
    ) {
        UUID authUserId = UUID.fromString(principal.getAttribute("sub"));
        return planningService.updateDriverPlanning(authUserId, id, request, token);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deletePlanning(
            @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
            @Parameter(hidden = true) @RequestHeader(name = "Authorization") String token,
            @PathVariable UUID id
    ) {
        UUID authUserId = UUID.fromString(principal.getAttribute("sub"));
        return planningService.deleteDriverPlanning(authUserId, id, token);
    }
}