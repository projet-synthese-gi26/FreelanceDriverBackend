package com.yowyob.template.infrastructure.adapters.inbound.rest;

import com.yowyob.template.domain.model.Product;
import com.yowyob.template.domain.ports.in.CreateProductUseCase;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.ProductRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.ProductResponse;
import com.yowyob.template.infrastructure.mappers.ProductMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final CreateProductUseCase useCase;
    private final ProductMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un produit", description = "Crée et sauvegarde un produit.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Produit créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public Mono<ProductResponse> create(@RequestBody @Valid ProductRequest request) {
        Product product = mapper.toDomain(request);
        return useCase.createProduct(product)
                .map(mapper::toResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un produit par ID", description = "Récupère un produit par son identifiant unique.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produit trouvé"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
    public Mono<ProductResponse> getProductById(@PathVariable UUID id) {
        return useCase.getProductById(id)
                .map(mapper::toResponse);
    }

    @GetMapping
    @Operation(summary = "Lister tous les produits", description = "Récupère la liste de tous les produits.")
    @ApiResponse(responseCode = "200", description = "Liste des produits")
    public Flux<ProductResponse> getAllProducts() {
        return useCase.getAllProducts()
                .map(mapper::toResponse);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un produit", description = "Met à jour un produit existant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produit mis à jour"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
    public Mono<ProductResponse> updateProduct(@PathVariable UUID id, @RequestBody @Valid ProductRequest request) {
        Product product = mapper.toDomain(request);
        return useCase.updateProduct(id, product)
                .map(mapper::toResponse);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un produit", description = "Supprime un produit par son identifiant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Produit supprimé"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteById(@PathVariable UUID id) {
        return useCase.deleteProduct(id);
    }

}