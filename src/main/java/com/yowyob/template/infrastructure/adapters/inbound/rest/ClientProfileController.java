package com.yowyob.template.infrastructure.adapters.inbound.rest;

import com.yowyob.template.domain.model.Address;
import com.yowyob.template.domain.model.BusinessActor;
import com.yowyob.template.domain.model.Contact;
import com.yowyob.template.application.service.ClientProfileService;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.AddressRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.ContactRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/client/profile")
@RequiredArgsConstructor
@Tag(name = "Client Profile", description = "Gestion du profil Client et de ses informations associées")
public class ClientProfileController {

    private final ClientProfileService clientProfileService;

    @GetMapping
    @Operation(summary = "Obtenir le profil", description = "Récupère les informations du profil Client (Authentifié).")
    public Mono<UserProfileResponse> getProfile(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                                @Parameter(hidden = true) @RequestHeader(name = "Authorization") String authHeader) {
        if (principal == null) {
            return Mono.error(new RuntimeException("Unauthorized"));
        }
        String sub = principal.getAttribute("sub");
        UUID userId = UUID.fromString(sub);

        return clientProfileService.getProfile(userId, authHeader)
                .map(profile -> UserProfileResponse.builder()
                        .user(profile.getUser())
                        .actor(profile.getActor())
                        .organisation(profile.getOrganisation())
                        .build());
    }

    @PutMapping
    @Operation(summary = "Mettre à jour le profil", description = "Met à jour les informations de base du client.")
    public Mono<BusinessActor> updateProfile(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                             @Parameter(hidden = true) @RequestHeader(name = "Authorization") String authHeader,
                                             @RequestBody BusinessActor actor) {
        String sub = principal.getAttribute("sub");
        UUID userId = UUID.fromString(sub);
        return clientProfileService.updateProfile(userId, actor, authHeader);
    }
    
    @GetMapping("/contacts")
    @Operation(summary = "Lister les contacts", description = "Récupère les contacts du client.")
    public Flux<Contact> getContacts(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                     @Parameter(hidden = true) @RequestHeader(name = "Authorization", required = false) String authHeader) {
        String sub = principal.getAttribute("sub");
        UUID userId = UUID.fromString(sub);
        return clientProfileService.getContacts(userId, authHeader);
    }

    @PostMapping("/addresses")
    @Operation(summary = "Ajouter une adresse", description = "Associe une nouvelle adresse au profil client.")
    public Mono<Address> addAddress(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                    @Parameter(hidden = true) @RequestHeader(name = "Authorization") String authHeader,
                                    @RequestBody AddressRequest request) {
        String sub = principal.getAttribute("sub");
        UUID userId = UUID.fromString(sub);

        Address address = Address.builder()
                .id(UUID.randomUUID())
                .type(request.type())
                .addressLine1(request.addressLine1())
                .addressLine2(request.addressLine2())
                .city(request.city())
                .state(request.state())
                .locality(request.locality())
                .zipCode(request.zipCode())
                .postalCode(request.postalCode())
                .poBox(request.poBox())
                .isDefault(request.isDefault())
                .neighborhood(request.neighborhood())
                .informalDescription(request.informalDescription())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .build();
        return clientProfileService.addAddress(userId, address, authHeader);
    }

    @PostMapping("/contacts")
    @Operation(summary = "Ajouter un contact", description = "Associe un nouveau contact au profil client.")
    public Mono<Contact> addContact(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                    @Parameter(hidden = true) @RequestHeader(name = "Authorization") String authHeader,
                                    @RequestBody ContactRequest request) {
        String sub = principal.getAttribute("sub");
        UUID userId = UUID.fromString(sub);

        Contact contact = Contact.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .title(request.title())
                .isEmailVerified(request.isEmailVerified())
                .isPhoneNumberVerified(request.isPhoneNumberVerified())
                .isFavorite(request.isFavorite())
                .phoneNumber(request.phoneNumber())
                .secondaryPhoneNumber(request.secondaryPhoneNumber())
                .faxNumber(request.faxNumber())
                .email(request.email())
                .secondaryEmail(request.secondaryEmail())
                .build();
        return clientProfileService.addContact(userId, contact, authHeader);
    }

    @PutMapping("/contacts/{id}")
    @Operation(summary = "Mettre à jour un contact", description = "Met à jour un contact existant du profil client.")
    public Mono<Contact> updateContact(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                       @Parameter(hidden = true) @RequestHeader(name = "Authorization") String authHeader,
                                       @PathVariable UUID id,
                                       @RequestBody ContactRequest request) {
        String sub = principal.getAttribute("sub");
        UUID userId = UUID.fromString(sub);

        Contact contact = Contact.builder()
                .id(id)
                .firstName(request.firstName())
                .lastName(request.lastName())
                .title(request.title())
                .isEmailVerified(request.isEmailVerified())
                .isPhoneNumberVerified(request.isPhoneNumberVerified())
                .isFavorite(request.isFavorite())
                .phoneNumber(request.phoneNumber())
                .secondaryPhoneNumber(request.secondaryPhoneNumber())
                .faxNumber(request.faxNumber())
                .email(request.email())
                .secondaryEmail(request.secondaryEmail())
                .build();
        return clientProfileService.updateContact(userId, contact, authHeader);
    }

    @DeleteMapping("/contacts/{id}")
    @Operation(summary = "Supprimer un contact", description = "Supprime un contact du profil client.")
    public Mono<Void> deleteContact(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                    @Parameter(hidden = true) @RequestHeader(name = "Authorization") String authHeader,
                                    @PathVariable UUID id) {
        String sub = principal.getAttribute("sub");
        UUID userId = UUID.fromString(sub);
        return clientProfileService.deleteContact(userId, id, authHeader);
    }

    @GetMapping("/addresses")
    @Operation(summary = "Lister les adresses", description = "Récupère les adresses du client.")
    public Flux<Address> getAddresses(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                      @Parameter(hidden = true) @RequestHeader(name = "Authorization") String authHeader) {
        String sub = principal.getAttribute("sub");
        UUID userId = UUID.fromString(sub);
        return clientProfileService.getAddresses(userId, authHeader);
    }

    @PutMapping("/addresses/{id}")
    @Operation(summary = "Mettre à jour une adresse", description = "Met à jour une adresse existante du profil client.")
    public Mono<Address> updateAddress(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                       @Parameter(hidden = true) @RequestHeader(name = "Authorization") String authHeader,
                                       @PathVariable UUID id,
                                       @RequestBody AddressRequest request) {
        String sub = principal.getAttribute("sub");
        UUID userId = UUID.fromString(sub);

        Address address = Address.builder()
                .id(id)
                .type(request.type())
                .addressLine1(request.addressLine1())
                .addressLine2(request.addressLine2())
                .city(request.city())
                .state(request.state())
                .locality(request.locality())
                .zipCode(request.zipCode())
                .postalCode(request.postalCode())
                .poBox(request.poBox())
                .isDefault(request.isDefault())
                .neighborhood(request.neighborhood())
                .informalDescription(request.informalDescription())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .build();
        return clientProfileService.updateAddress(userId, address, authHeader);
    }

    @DeleteMapping("/addresses/{id}")
    @Operation(summary = "Supprimer une adresse", description = "Supprime une adresse du profil client.")
    public Mono<Void> deleteAddress(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                    @Parameter(hidden = true) @RequestHeader(name = "Authorization") String authHeader,
                                    @PathVariable UUID id) {
        String sub = principal.getAttribute("sub");
        UUID userId = UUID.fromString(sub);
        return clientProfileService.deleteAddress(userId, id, authHeader);
    }
}

