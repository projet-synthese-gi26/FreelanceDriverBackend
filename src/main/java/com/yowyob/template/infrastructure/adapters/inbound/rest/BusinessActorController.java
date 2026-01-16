package com.yowyob.template.infrastructure.adapters.inbound.rest;

import com.yowyob.template.domain.model.BusinessActor;
import com.yowyob.template.domain.ports.in.createBusinessActorUseCase;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.BusinessActorRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.BusinessActorResponse;
import com.yowyob.template.infrastructure.mappers.BusinessActorMapper;
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
@RequestMapping("/api/v1/business-actors")
@RequiredArgsConstructor
public class BusinessActorController {
    private final createBusinessActorUseCase useCase;
    private final BusinessActorMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un acteur économique", description = "Crée et sauvegarde un acteur économique.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Acteur créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public Mono<BusinessActorResponse> create(@RequestBody @Valid BusinessActorRequest request) {
        BusinessActor businessActor = mapper.toDomain(request);
        return useCase.createBusinessActor(businessActor)
                .map(mapper::toResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un acteur par ID", description = "Récupère un acteur économique par son identifiant unique.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Acteur trouvé"),
        @ApiResponse(responseCode = "404", description = "Acteur non trouvé")
    })
    public Mono<BusinessActorResponse> getById(@PathVariable UUID id) {
        return useCase.getBusinessActorById(id)
                .map(mapper::toResponse);
    }

    @GetMapping
    @Operation(summary = "Lister tous les acteurs économiques", description = "Récupère la liste de tous les acteurs économiques.")
    @ApiResponse(responseCode = "200", description = "Liste des acteurs économiques")
    public Flux<BusinessActorResponse> getAll() {
        return useCase.getAllBusinessActors()
                .map(mapper::toResponse);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un acteur économique", description = "Met à jour un acteur économique existant.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Acteur mis à jour"),
        @ApiResponse(responseCode = "404", description = "Acteur non trouvé")
    })
    public Mono<BusinessActorResponse> update(@PathVariable UUID id, @RequestBody @Valid BusinessActorRequest request) {
        BusinessActor businessActor = mapper.toDomain(request);
        return useCase.updateBusinessActor(id, businessActor)
                .map(mapper::toResponse);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un acteur économique", description = "Supprime un acteur économique par son identifiant.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Acteur supprimé"),
        @ApiResponse(responseCode = "404", description = "Acteur non trouvé")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable UUID id) {
        return useCase.deleteBusinessActor(id);
    }
}
