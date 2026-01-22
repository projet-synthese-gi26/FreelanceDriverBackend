package com.yowyob.template.infrastructure.adapters.inbound.rest;

import com.yowyob.template.domain.model.Address;
import com.yowyob.template.domain.model.Certification;
import com.yowyob.template.domain.model.Contact;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.*;
import com.yowyob.template.application.service.DriverProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/driver/profile")
@RequiredArgsConstructor
@Tag(name = "Driver Profile", description = "Gestion du profil Chauffeur et de ses certifications")
public class DriverProfileController {

    private final DriverProfileService service;

    @GetMapping
    @Operation(summary = "Obtenir le profil", description = "Récupère les informations du profil Chauffeur (Authentifié).")
    public Mono<UserProfileResponse> getProfile() {
        // Need to get current user ID from security context
        // For now placeholder assuming ID passed or context handler in service
        return Mono.error(new UnsupportedOperationException("Security Context needed"));
    }

    @PutMapping("/address")
    @Operation(summary = "Mettre à jour l'adresse", description = "Modifie l'adresse du Chauffeur.")
    public Mono<AddressResponse> updateAddress(@RequestBody AddressRequest request) {
         // Placeholder for integration
         return Mono.empty();
    }
    
    @PostMapping("/contact")
    @Operation(summary = "Ajouter un contact", description = "Ajoute un contact à l'organisation du Chauffeur.")
    public Mono<ContactResponse> addContact(@RequestBody ContactRequest request) {
         return Mono.empty();
    }

    @PostMapping("/certification-request")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Demande de certification", description = "Soumet une nouvelle demande de certification.")
    public Mono<CertificationResponse> requestCertification(@RequestBody CertificationRequest request) {
        return service.requestCertification(request.organizationId(), request.name())
            .map(cert -> new CertificationResponse(
                cert.getId(),
                cert.getOrganizationId(),
                cert.getName(),
                cert.getType(),
                cert.getDescription(),
                cert.getObtainementDate(),
                cert.getCreatedAt()
            ));
    }
}
