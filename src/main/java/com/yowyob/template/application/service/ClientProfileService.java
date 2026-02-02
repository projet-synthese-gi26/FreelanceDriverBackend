package com.yowyob.template.application.service;

import com.yowyob.template.domain.model.*;
import com.yowyob.template.domain.ports.out.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientProfileService {

    private final BusinessActorRepositoryPort businessActorRepository;
    private final OrganisationRepositoryPort organisationRepository;
    private final ContactRepositoryPort contactRepository;
    private final UserRepositoryPort userRepository;
    private final AddressRepositoryPort addressRepository;

    public Mono<ClientProfile> getProfile(UUID userId, String jwtToken) {
        String token = jwtToken != null && jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;

        return businessActorRepository.findByUserId(userId, token)
                .flatMap(actor -> Mono.zip(
                        userRepository.findById(userId, token),
                        organisationRepository.findByActorId(actor.getId(), token)
                            .switchIfEmpty(Mono.just(ClientOrganisation.builder().build()))
                ).map(tuple -> ClientProfile.builder()
                        .user(tuple.getT1())
                        .actor(actor)
                        .organisation(tuple.getT2().getId() == null ? null : tuple.getT2())
                        .build()));
    }

    public Mono<BusinessActor> updateProfile(UUID userId, BusinessActor updatedActor, String jwtToken) {
        String token = jwtToken != null && jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;
        return businessActorRepository.findByUserId(userId, token)
                .flatMap(existing -> {
                    // Update allowed fields
                    existing.setDisplayName(updatedActor.getDisplayName() != null ? updatedActor.getDisplayName() : existing.getDisplayName());
                    existing.setPhoneNumber(updatedActor.getPhoneNumber() != null ? updatedActor.getPhoneNumber() : existing.getPhoneNumber());
                    // ... other fields
                    return businessActorRepository.save(existing, token);
                });
    }

    public Flux<Contact> getContacts(UUID userId, String jwtToken) {
        String token = jwtToken != null && jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;

        return businessActorRepository.findByUserId(userId, token)
                .flatMap(actor -> organisationRepository.findByActorId(actor.getId(), token))
                .flatMapMany(org -> contactRepository.findAllByContactableId(org.getId(), token));
    }

    public Mono<Contact> addContact(UUID userId, Contact contact, String jwtToken) {
        String token = jwtToken != null && jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;

        return businessActorRepository.findByUserId(userId, token)
                .switchIfEmpty(Mono.error(new RuntimeException("Business Actor not found")))
                .flatMap(actor -> organisationRepository.findByActorId(actor.getId(), token))
                .switchIfEmpty(Mono.error(new RuntimeException("Organisation not found")))
                .flatMap(org -> {
                    contact.setContactableId(org.getId());
                    contact.setContactableType("ORGANIZATION");
                    return contactRepository.save(contact, token);
                });
    }

    public Mono<Contact> updateContact(UUID userId, Contact contact, String jwtToken) {
        String token = jwtToken != null && jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;
        
        return businessActorRepository.findByUserId(userId, token)
                .flatMap(actor -> organisationRepository.findByActorId(actor.getId(), token))
                .switchIfEmpty(Mono.error(new RuntimeException("Organisation not found")))
                .flatMap(org -> {
                    contact.setContactableId(org.getId());
                    contact.setContactableType("ORGANIZATION");
                    return contactRepository.update(contact, token);
                });
    }

    public Mono<Address> addAddress(UUID userId, Address address, String jwtToken) {
        String token = jwtToken != null && jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;
        
        return businessActorRepository.findByUserId(userId, token)
                .flatMap(actor -> organisationRepository.findByActorId(actor.getId(), token))
                .switchIfEmpty(Mono.error(new RuntimeException("Organisation not found")))
                .flatMap(org -> {
                    address.setAddressableId(org.getId());
                    address.setAddressableType("ORGANIZATION");
                    return addressRepository.save(address, token);
                });
    }

    public Mono<Address> updateAddress(UUID userId, Address address, String jwtToken) {
        String token = jwtToken != null && jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;
        
        return businessActorRepository.findByUserId(userId, token)
                .flatMap(actor -> organisationRepository.findByActorId(actor.getId(), token))
                .switchIfEmpty(Mono.error(new RuntimeException("Organisation not found")))
                .flatMap(org -> {
                    address.setAddressableId(org.getId());
                    address.setAddressableType("ORGANIZATION");
                    return addressRepository.update(address, token);
                });
    }

    public Mono<Void> deleteAddress(UUID userId, UUID addressId, String jwtToken) {
        String token = jwtToken != null && jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;
        return addressRepository.deleteById(addressId, token);
    }
    
    public Mono<Void> deleteContact(UUID userId, UUID contactId, String jwtToken) {
        String token = jwtToken != null && jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;
        return contactRepository.deleteById(contactId, token);
    }

    public Flux<Address> getAddresses(UUID userId, String jwtToken) {
        String token = jwtToken != null && jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;
        return businessActorRepository.findByUserId(userId, token)
                .flatMap(actor -> organisationRepository.findByActorId(actor.getId(), token))
                .flatMapMany(org -> addressRepository.findAllByAddressableId(org.getId(), token));
    }
}
