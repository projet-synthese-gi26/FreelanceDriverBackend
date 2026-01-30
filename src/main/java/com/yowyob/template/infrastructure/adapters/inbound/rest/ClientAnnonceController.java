package com.yowyob.template.infrastructure.adapters.inbound.rest;

import com.yowyob.template.application.service.ClientAnnonceService;
import com.yowyob.template.domain.model.Product;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.request.CreateAnnonceRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.request.UpdateAnnonceRequest;
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
@RequestMapping("/api/v1/client/annonces")
@RequiredArgsConstructor
@Tag(name = "Client Annonces", description = "Gestion des annonces (demandes de trajet) pour les Clients")
public class ClientAnnonceController {
    
    private final ClientAnnonceService annonceService;
    private final com.yowyob.template.application.service.ProductService productService;

    @PostMapping
    @Operation(summary = "Créer une annonce", description = "Publie une nouvelle demande de trajet pour le client connecté.")
    public Mono<Product> createAnnonce(
            @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
            @Parameter(hidden = true) @RequestHeader(name = "Authorization") String token,
            @Valid @RequestBody CreateAnnonceRequest request
    ) {
        UUID authUserId = UUID.fromString(principal.getAttribute("sub"));
        return annonceService.createClientAnnonce(authUserId, request, token);
    }

    @GetMapping
    @Operation(summary = "Lister les annonces", description = "Récupère la liste des demandes de trajet pour le client connecté.")
    public Flux<Product> listAnnonces(
            @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
            @Parameter(hidden = true) @RequestHeader(name = "Authorization") String token
    ) {
        UUID authUserId = UUID.fromString(principal.getAttribute("sub"));
        return annonceService.listClientAnnonces(authUserId, token);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une annonce", description = "Récupère une demande de trajet spécifique pour le client connecté.")
    public Mono<Product> getAnnonce(
            @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
            @Parameter(hidden = true) @RequestHeader(name = "Authorization") String token,
            @PathVariable UUID id
    ) {
        UUID authUserId = UUID.fromString(principal.getAttribute("sub"));
        return annonceService.getClientAnnonce(authUserId, id, token);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une annonce", description = "Met à jour une demande de trajet spécifique pour le client connecté.")
    public Mono<Product> updateAnnonce(
            @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
            @Parameter(hidden = true) @RequestHeader(name = "Authorization") String token,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAnnonceRequest request
    ) {
        UUID authUserId = UUID.fromString(principal.getAttribute("sub"));
        return annonceService.updateClientAnnonce(authUserId, id, request, token);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une annonce", description = "Supprime une demande de trajet spécifique pour le client connecté.")
    public Mono<Void> deleteAnnonce(
            @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
            @Parameter(hidden = true) @RequestHeader(name = "Authorization") String token,
            @PathVariable UUID id
    ) {
        UUID authUserId = UUID.fromString(principal.getAttribute("sub"));
        return annonceService.deleteClientAnnonce(authUserId, id, token);
    }
}