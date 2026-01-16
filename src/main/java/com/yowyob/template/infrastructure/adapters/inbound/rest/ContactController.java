package com.yowyob.template.infrastructure.adapters.inbound.rest;

import com.yowyob.template.domain.model.Contact;
import com.yowyob.template.domain.ports.in.CreateContactUseCase;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.ContactRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.ContactResponse;
import com.yowyob.template.infrastructure.mappers.ContactMapper;
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
@RequestMapping("/api/v1/contacts")
@RequiredArgsConstructor
public class ContactController {
    private final CreateContactUseCase useCase;
    private final ContactMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un contact", description = "Crée et sauvegarde un contact.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Contact créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public Mono<ContactResponse> create(@RequestBody @Valid ContactRequest request) {
        Contact contact = mapper.toDomain(request);
        return useCase.createContact(contact)
                .map(mapper::toResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un contact par ID", description = "Récupère un contact par son identifiant unique.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contact trouvé"),
        @ApiResponse(responseCode = "404", description = "Contact non trouvé")
    })
    public Mono<ContactResponse> getById(@PathVariable UUID id) {
        return useCase.getContactById(id)
                .map(mapper::toResponse);
    }

    @GetMapping
    @Operation(summary = "Lister tous les contacts", description = "Récupère la liste de tous les contacts.")
    @ApiResponse(responseCode = "200", description = "Liste des contacts")
    public Flux<ContactResponse> getAll() {
        return useCase.getAllContacts()
                .map(mapper::toResponse);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un contact", description = "Met à jour un contact existant.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contact mis à jour"),
        @ApiResponse(responseCode = "404", description = "Contact non trouvé")
    })
    public Mono<ContactResponse> update(@PathVariable UUID id, @RequestBody @Valid ContactRequest request) {
        Contact contact = mapper.toDomain(request);
        return useCase.updateContact(id, contact)
                .map(mapper::toResponse);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un contact", description = "Supprime un contact par son identifiant.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Contact supprimé"),
        @ApiResponse(responseCode = "404", description = "Contact non trouvé")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable UUID id) {
        return useCase.deleteContact(id);
    }
}
