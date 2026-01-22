package com.yowyob.template.infrastructure.adapters.inbound.rest;

import com.yowyob.template.domain.model.Settings;
import com.yowyob.template.domain.ports.in.createSettingsUseCase;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.SettingsRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.SettingsResponse;
import com.yowyob.template.infrastructure.mappers.SettingsMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
@Tag(name = "Settings", description = "Gestion des paramètres utilisateurs (Préférences, Thèmes, Notifications)")
public class SettingsController {
    private final createSettingsUseCase useCase;
    private final SettingsMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer des paramètres", description = "Crée et sauvegarde des paramètres pour un utilisateur.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Paramètres créés avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public Mono<SettingsResponse> create(@RequestBody @Valid SettingsRequest request) {
        Settings settings = mapper.toDomain(request);
        return useCase.createSettings(settings)
                .map(mapper::toResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir des paramètres par ID", description = "Récupère des paramètres par leur identifiant unique.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Paramètres trouvés"),
        @ApiResponse(responseCode = "404", description = "Paramètres non trouvés")
    })
    public Mono<SettingsResponse> getById(@PathVariable UUID id) {
        return useCase.getSettingsById(id)
                .map(mapper::toResponse);
    }

    @GetMapping
    @Operation(summary = "Lister tous les paramètres", description = "Récupère la liste de tous les paramètres.")
    @ApiResponse(responseCode = "200", description = "Liste des paramètres")
    public Flux<SettingsResponse> getAll() {
        return useCase.getAllSettings()
                .map(mapper::toResponse);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour des paramètres", description = "Met à jour des paramètres existants.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Paramètres mis à jour"),
        @ApiResponse(responseCode = "404", description = "Paramètres non trouvés")
    })
    public Mono<SettingsResponse> update(@PathVariable UUID id, @RequestBody @Valid SettingsRequest request) {
        Settings settings = mapper.toDomain(request);
        return useCase.updateSettings(id, settings)
                .map(mapper::toResponse);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer des paramètres", description = "Supprime des paramètres par leur identifiant.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Paramètres supprimés"),
        @ApiResponse(responseCode = "404", description = "Paramètres non trouvés")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable UUID id) {
        return useCase.deleteSettings(id);
    }
}
