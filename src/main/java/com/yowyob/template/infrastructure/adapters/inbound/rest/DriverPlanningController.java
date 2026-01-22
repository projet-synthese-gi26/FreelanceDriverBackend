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
@RequestMapping("/api/v1/driver/plannings")
@RequiredArgsConstructor
@Tag(name = "Driver Planning", description = "Gestion des plannings (Produits) pour les Chauffeurs")
public class DriverPlanningController {
    private final CreateProductUseCase productService;

    @PostMapping
    @Operation(summary = "Créer un planning", description = "Crée un nouveau créneau ou produit de transport pour l'organisation du chauffeur.")
    public Mono<Product> createPlanning(@RequestParam UUID organisationId, @RequestBody Map<String, Object> params) {
        return productService.createProductForOrganisation(organisationId, params);
    }

    @GetMapping
    @Operation(summary = "Lister les plannings", description = "Récupère tous les plannings disponibles (filtrage à implémenter).")
    public Flux<Product> getAllPlannings() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un planning", description = "Récupère les détails d'un planning spécifique par ID.")
    public Mono<Product> getPlanning(@PathVariable UUID id) {
        return productService.getProductById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un planning", description = "Modifie les informations d'un planning existant.")
    public Mono<Product> updatePlanning(@PathVariable UUID id, @RequestBody Product product) {
        return productService.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un planning", description = "Supprime un planning du système.")
    public Mono<Void> deletePlanning(@PathVariable UUID id) {
        return productService.deleteProduct(id);
    }
}
