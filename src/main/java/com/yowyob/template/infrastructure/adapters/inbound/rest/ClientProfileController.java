package com.yowyob.template.infrastructure.adapters.inbound.rest;

import com.yowyob.template.domain.model.Address;
import com.yowyob.template.domain.model.BusinessActor;
import com.yowyob.template.domain.model.Contact;
import com.yowyob.template.domain.ports.in.createAddressUseCase;
import com.yowyob.template.domain.ports.in.createBusinessActorUseCase;
import com.yowyob.template.domain.ports.in.CreateContactUseCase;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.AddressRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.ContactRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/client/profile")
@RequiredArgsConstructor
@Tag(name = "Client Profile", description = "Gestion du profil Client et de ses informations associées")
public class ClientProfileController {

    private final createBusinessActorUseCase businessActorService;
    private final createAddressUseCase addressService;
    private final CreateContactUseCase contactService;

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir le profil", description = "Récupère les informations de base de l'acteur Business (Client) par ID.")
    public Mono<BusinessActor> getProfile(@PathVariable UUID id) {
        return businessActorService.getBusinessActorById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour le profil", description = "Met à jour les informations de base du client.")
    public Mono<BusinessActor> updateProfile(@PathVariable UUID id, @RequestBody BusinessActor actor) {
        return businessActorService.updateBusinessActor(id, actor);
    }

    @PostMapping("/{id}/addresses")
    @Operation(summary = "Ajouter une adresse", description = "Associe une nouvelle adresse au profil client.")
    public Mono<Address> addAddress(@PathVariable UUID id, @RequestBody AddressRequest request) {
        Address address = Address.builder()
                .id(UUID.randomUUID())
                .addressableId(id)
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
        return addressService.createAddress(address);
    }

    @PostMapping("/{id}/contacts")
    @Operation(summary = "Ajouter un contact", description = "Associe un nouveau contact au profil client.")
    public Mono<Contact> addContact(@PathVariable UUID id, @RequestBody ContactRequest request) {
        Contact contact = Contact.builder()
                .id(UUID.randomUUID())
                .contactableId(id)
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
        return contactService.createContact(contact);
    }
}
