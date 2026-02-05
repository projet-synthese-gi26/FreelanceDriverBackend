package com.yowyob.template.infrastructure.adapters.inbound.rest;

import com.yowyob.template.application.service.DriverPlanningService;
import com.yowyob.template.domain.model.Product;
import com.yowyob.template.domain.model.Planning;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.CreatePlanningRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.request.UpdatePlanningRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
            ServerWebExchange exchange,
            @Valid @RequestBody CreatePlanningRequest request
    ) {
        UUID authUserId = UUID.fromString(principal.getAttribute("sub"));
        return planningService.createDriverPlanning(authUserId, request, extractAuthToken(exchange));
    }

    @GetMapping("/published")
    @Operation(summary = "Lister les plannings publiés", description = "Récupère la liste de tous les plannings ayant le statut 'Published'.")
    public Flux<Product> listPublishedPlannings() {
        return planningService.getPublishedPlannings();
    }

    @GetMapping
    public Flux<Product> listPlannings(
            @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
            ServerWebExchange exchange
    ) {
        if (principal == null) {
            return productService.getAllProducts()
                    .filter(product -> product instanceof Planning);
        }
        UUID authUserId = UUID.fromString(principal.getAttribute("sub"));
        return planningService.listDriverPlannings(authUserId, extractAuthToken(exchange));
    }

    @GetMapping("/user/{driverId}")
    @Operation(summary = "Lister les plannings par driverId", description = "Récupère la liste des plannings pour un chauffeur donné (admin).")
    public Flux<Product> listPlanningsByDriverId(@PathVariable UUID driverId) {
        return planningService.listDriverPlanningsByDriverId(driverId);
    }

    @GetMapping("/{id}")
    public Mono<Product> getPlanning(
            @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
            ServerWebExchange exchange,
            @PathVariable UUID id
    ) {
        UUID authUserId = UUID.fromString(principal.getAttribute("sub"));
        return planningService.getDriverPlanning(authUserId, id, extractAuthToken(exchange));
    }

    @PutMapping("/{id}")
    public Mono<Product> updatePlanning(
            @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
            ServerWebExchange exchange,
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePlanningRequest request
    ) {
        UUID authUserId = UUID.fromString(principal.getAttribute("sub"));
        return planningService.updateDriverPlanning(authUserId, id, request, extractAuthToken(exchange));
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deletePlanning(
            @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
            ServerWebExchange exchange,
            @PathVariable UUID id
    ) {
        UUID authUserId = UUID.fromString(principal.getAttribute("sub"));
        return planningService.deleteDriverPlanning(authUserId, id, extractAuthToken(exchange));
    }

    private String extractAuthToken(ServerWebExchange exchange) {
        return exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    }
}