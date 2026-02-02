package com.yowyob.template.infrastructure.adapters.inbound.rest;

import com.yowyob.template.domain.model.Address;
import com.yowyob.template.domain.model.Certification;
import com.yowyob.template.domain.model.Contact;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.*;
import com.yowyob.template.application.service.DriverProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
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
    public Mono<UserProfileResponse> getProfile(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                                @Parameter(hidden = true) @RequestHeader(name = "Authorization") String authHeader) {
        if (principal == null) {
            return Mono.error(new RuntimeException("Unauthorized"));
        }
        
        String sub = principal.getAttribute("sub");
        UUID userId = UUID.fromString(sub);

        return service.getProfile(userId, authHeader)
                .map(profile -> UserProfileResponse.builder()
                        .user(profile.getUser())
                        .actor(profile.getActor())
                        .organisation(profile.getOrganisation())
                        .build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtenir le profil par userId", description = "Récupère les informations du profil Chauffeur par l'id utilisateur.")
    public Mono<UserProfileResponse> getProfileByUserId(@PathVariable UUID userId,
                                                        @Parameter(hidden = true) @RequestHeader(name = "Authorization") String authHeader) {
        return service.getProfile(userId, authHeader)
                .map(profile -> UserProfileResponse.builder()
                        .user(profile.getUser())
                        .actor(profile.getActor())
                        .organisation(profile.getOrganisation())
                        .build());
    }

    @PutMapping("/addresses/{id}")
    @Operation(summary = "Mettre à jour une adresse", description = "Modifie une adresse du Chauffeur.")
    public Mono<AddressResponse> updateAddress(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
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

        return service.updateAddress(userId, address, authHeader)
                .map(this::mapToAddressResponse);

    }
    
    @PostMapping("/contacts")
    @Operation(summary = "Ajouter un contact", description = "Ajoute un contact à l'organisation du Chauffeur.")
    public Mono<ContactResponse> addContact(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal, 
                                            @Parameter(hidden = true) @RequestHeader(name = "Authorization", required = false) String authHeader,
                                            @RequestBody ContactRequest request) {
        String sub = principal.getAttribute("sub");
        UUID userId = UUID.fromString(sub);

        Contact contact = Contact.builder()
                .contactableType(request.contactableType())
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

         return service.addContact(userId, contact, authHeader)
                .map(this::mapToContactResponse);
    }
    
    @PutMapping("/contacts/{id}")
    @Operation(summary = "Mettre à jour un contact", description = "Modifie un contact existant de l'organisation du Chauffeur.")
    public Mono<ContactResponse> updateContact(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal, 
                                            @Parameter(hidden = true) @RequestHeader(name = "Authorization", required = false) String authHeader,
                                            @PathVariable UUID id,
                                            @RequestBody ContactRequest request) {
        String sub = principal.getAttribute("sub");
        UUID userId = UUID.fromString(sub);

        Contact contact = Contact.builder()
                .id(id)
                .contactableType(request.contactableType())
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

         return service.updateContact(userId, contact, authHeader)
                .map(this::mapToContactResponse);
    }

    @GetMapping("/contacts")
    @Operation(summary = "Lister les contacts", description = "Récupère les contacts de l'organisation du Chauffeur.")
    public Flux<ContactResponse> getContacts(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                             @Parameter(hidden = true) @RequestHeader(name = "Authorization", required = false) String authHeader) {
        String sub = principal.getAttribute("sub");
        UUID userId = UUID.fromString(sub);
        
        return service.getContacts(userId, authHeader)
                .map(this::mapToContactResponse);
    }

    @DeleteMapping("/contacts/{id}")
    @Operation(summary = "Supprimer un contact", description = "Supprime un contact du profil chauffeur.")
    public Mono<Void> deleteContact(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                    @Parameter(hidden = true) @RequestHeader(name = "Authorization") String authHeader,
                                    @PathVariable UUID id) {
        String sub = principal.getAttribute("sub");
        UUID userId = UUID.fromString(sub);
        return service.deleteContact(userId, id, authHeader);
    }

    @GetMapping("/addresses")
    @Operation(summary = "Lister les adresses", description = "Récupère les adresses du chauffeur.")
    public Flux<AddressResponse> getAddresses(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                              @Parameter(hidden = true) @RequestHeader(name = "Authorization") String authHeader) {
        String sub = principal.getAttribute("sub");
        UUID userId = UUID.fromString(sub);
        return service.getAddresses(userId, authHeader)
                .map(this::mapToAddressResponse);
    }

    @PostMapping("/addresses")
    @Operation(summary = "Ajouter une adresse", description = "Ajoute une adresse au profil chauffeur.")
    public Mono<AddressResponse> addAddress(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                            @Parameter(hidden = true) @RequestHeader(name = "Authorization") String authHeader,
                                            @RequestBody AddressRequest request) {
        String sub = principal.getAttribute("sub");
        UUID userId = UUID.fromString(sub);

        Address address = Address.builder()
                .addressableType(request.addressableType())
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
                
        return service.addAddress(userId, address, authHeader)
                .map(this::mapToAddressResponse);
    }

    @DeleteMapping("/addresses/{id}")
    @Operation(summary = "Supprimer une adresse", description = "Supprime une adresse du profil chauffeur.")
    public Mono<Void> deleteAddress(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                    @Parameter(hidden = true) @RequestHeader(name = "Authorization") String authHeader,
                                    @PathVariable UUID id) {
        String sub = principal.getAttribute("sub");
        UUID userId = UUID.fromString(sub);
        return service.deleteAddress(userId, id, authHeader);
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

    private AddressResponse mapToAddressResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getAddressableType(),
                address.getType(),
                address.getAddressLine1(),
                address.getAddressLine2(),
                address.getCity(),
                address.getState(),
                address.getLocality(),
                address.getZipCode(),
                address.getPostalCode(),
                address.getPoBox(),
                address.getIsDefault(),
                address.getNeighborhood(),
                address.getInformalDescription(),
                address.getLatitude(),
                address.getLongitude(),
                null, // CreatedAt
                null, // UpdatedAt
                null // DeletedAt
        );
    }
    private ContactResponse mapToContactResponse(Contact contact) {
        return new ContactResponse(
                contact.getId(),
                contact.getContactableId(),
                contact.getContactableType(),
                contact.getFirstName(),
                contact.getLastName(),
                contact.getTitle(),
                contact.getIsEmailVerified(),
                contact.getIsPhoneNumberVerified(),
                contact.getIsFavorite(),
                contact.getPhoneNumber(),
                contact.getSecondaryPhoneNumber(),
                contact.getFaxNumber(),
                contact.getEmail(),
                contact.getSecondaryEmail(),
                contact.getEmailVerifiedAt(),
                contact.getPhoneVerifiedAt(),
                contact.getCreatedAt(),
                contact.getUpdatedAt(),
                contact.getDeletedAt()
        );
    }
}
