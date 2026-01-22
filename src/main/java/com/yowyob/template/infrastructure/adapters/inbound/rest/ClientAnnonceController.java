package com.yowyob.template.infrastructure.adapters.inbound.rest;

import com.yowyob.template.domain.model.Product;
import com.yowyob.template.domain.ports.in.CreateProductUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/client/annonces")
@RequiredArgsConstructor
@Tag(name = "Client Annonces", description = "Gestion des annonces (demandes de trajet) pour les Clients")
public class ClientAnnonceController {
    private final CreateProductUseCase productService;

    @PostMapping
    @Operation(summary = "Créer une annonce", description = "Publie une demande de trajet ou annonce pour l'organisation du client.")
    public Mono<Product> createAnnonce(@RequestParam UUID organisationId, @RequestBody Map<String, Object> params) {
        return productService.createProductForOrganisation(organisationId, params);
    }

    @GetMapping
    @Operation(summary = "Lister les annonces", description = "Récupère toutes les annonces disponibles.")
    public Flux<Product> getAllAnnonces() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une annonce", description = "Récupère les détails d'une annonce spécifique par ID.")
    public Mono<Product> getAnnonce(@PathVariable UUID id) {
        return productService.getProductById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une annonce", description = "Modifie les informations d'une annonce existante.")
    public Mono<Product> updateAnnonce(@PathVariable UUID id, @RequestBody Product product) {
        return productService.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une annonce", description = "Supprime une annonce du système.")
    public Mono<Void> deleteAnnonce(@PathVariable UUID id) {
        return productService.deleteProduct(id);
    }
}
