package com.yowyob.template.infrastructure.adapters.inbound.rest;

import com.yowyob.template.domain.model.Address;
import com.yowyob.template.domain.ports.in.createAddressUseCase;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.AddressRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.AddressResponse;
import com.yowyob.template.infrastructure.mappers.AddressMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final createAddressUseCase useCase;
    private final AddressMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer une adresse", description = "Crée et sauvegarde une adresse.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Adresse créée avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public Mono<AddressResponse> create(@RequestBody @Valid AddressRequest request) {
        Address address = mapper.toDomain(request);
        return useCase.createAddress(address)
                .map(mapper::toResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une adresse par ID", description = "Récupère une adresse par son identifiant unique.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Adresse trouvée"),
        @ApiResponse(responseCode = "404", description = "Adresse non trouvée")
    })
    public Mono<AddressResponse> getById(@PathVariable UUID id) {
        return useCase.getAddressById(id)
                .map(mapper::toResponse);
    }

    @GetMapping
    @Operation(summary = "Lister toutes les adresses", description = "Récupère la liste de toutes les adresses.")
    @ApiResponse(responseCode = "200", description = "Liste des adresses")
    public Flux<AddressResponse> getAll() {
        return useCase.getAllAddresses()
                .map(mapper::toResponse);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une adresse", description = "Met à jour une adresse existante.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Adresse mise à jour"),
        @ApiResponse(responseCode = "404", description = "Adresse non trouvée")
    })
    public Mono<AddressResponse> update(@PathVariable UUID id, @RequestBody @Valid AddressRequest request) {
        Address address = mapper.toDomain(request);
        return useCase.updateAddress(id, address)
                .map(mapper::toResponse);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une adresse", description = "Supprime une adresse par son identifiant.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Adresse supprimée"),
        @ApiResponse(responseCode = "404", description = "Adresse non trouvée")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable UUID id) {
        return useCase.deleteAddress(id);
    }
}
