package com.yowyob.template.application.service;

import com.yowyob.template.domain.model.Address;
import com.yowyob.template.domain.model.Certification;
import com.yowyob.template.domain.model.Contact;
import com.yowyob.template.domain.model.DriverProfile;
import com.yowyob.template.domain.model.Organisation;
import com.yowyob.template.domain.model.User;
import com.yowyob.template.domain.model.DriverOrganisation; 
import com.yowyob.template.domain.ports.out.AddressRepositoryPort;
import com.yowyob.template.domain.ports.out.BusinessActorRepositoryPort;
import com.yowyob.template.domain.ports.out.CertificationRepositoryPort;
import com.yowyob.template.domain.ports.out.ContactRepositoryPort;
import com.yowyob.template.domain.ports.out.OrganisationRepositoryPort;
import com.yowyob.template.domain.ports.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DriverProfileService {

    private final CertificationRepositoryPort certificationRepository;
    private final BusinessActorRepositoryPort businessActorRepository;
    private final OrganisationRepositoryPort organisationRepository;
    private final ContactRepositoryPort contactRepository;
    private final UserRepositoryPort userRepository;
    private final AddressRepositoryPort addressRepository;


    public Mono<DriverProfile> getProfile(UUID userId, String jwtToken) {
        String token = jwtToken != null && jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;

        return businessActorRepository.findByUserId(userId, token)
                .flatMap(actor -> Mono.zip(
                        userRepository.findById(userId, token),
                        organisationRepository.findByActorId(actor.getId(), token)
                            .switchIfEmpty(Mono.just(DriverOrganisation.builder().build()))
                ).map(tuple -> DriverProfile.builder()
                        .user(tuple.getT1())
                        .actor(actor)
                        .organisation(tuple.getT2().getId() == null ? null : tuple.getT2())
                        .build()));
    }

    public Mono<Address> updateAddress(UUID userId, Address address, String jwtToken) {
        String token = jwtToken != null && jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;

        return businessActorRepository.findByUserId(userId, token)
                .switchIfEmpty(Mono.error(new RuntimeException("Business Actor not found for user: " + userId)))
                .flatMap(actor -> {
                    address.setAddressableId(actor.getId());
                    address.setAddressableType("BUSINESS_ACTOR"); 
                    return addressRepository.update(address, token);
                });
    }

    public Mono<Address> addAddress(UUID userId, Address address, String jwtToken) {
        String token = jwtToken != null && jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;

        return businessActorRepository.findByUserId(userId, token)
                .switchIfEmpty(Mono.error(new RuntimeException("Business Actor not found for user: " + userId)))
                .flatMap(actor -> {
                    address.setAddressableId(actor.getId());
                    address.setAddressableType("BUSINESS_ACTOR"); 
                    return addressRepository.save(address, token);
                });
    }

    public Mono<Contact> updateContact(UUID userId, Contact contact, String jwtToken) {
        String token = jwtToken != null && jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;
        
        return businessActorRepository.findByUserId(userId, token)
                .switchIfEmpty(Mono.error(new RuntimeException("Business Actor not found for user: " + userId)))
                .flatMap(actor -> organisationRepository.findByActorId(actor.getId(), token))
                .switchIfEmpty(Mono.error(new RuntimeException("Organisation not found for actor")))
                .flatMap(org -> {
                    contact.setContactableId(org.getId());
                    contact.setContactableType("ORGANISATION"); 
                    return contactRepository.update(contact, token);
                });
    }

    public Flux<Address> getAddresses(UUID userId, String jwtToken) {
        String token = jwtToken != null && jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;
        return businessActorRepository.findByUserId(userId, token)
            .flatMapMany(actor -> addressRepository.findAllByAddressableId(actor.getId(), token));
    }

    public Mono<Void> deleteAddress(UUID userId, UUID addressId, String jwtToken) {
        String token = jwtToken != null && jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;
        return addressRepository.deleteById(addressId, token);
    }

    public Mono<Void> deleteContact(UUID userId, UUID contactId, String jwtToken) {
        String token = jwtToken != null && jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;
        return contactRepository.deleteById(contactId, token);
    }

    public Mono<Certification> requestCertification(UUID orgId, String syndicateName) {
        // Logic: 
        // 1. Verify Org exists / is Driver Org?
        // 2. Create Certification object
        Certification cert = Certification.builder()
                .organizationId(orgId)
                .name(syndicateName)
                .type("SYNDICATE_LABEL") 
                .obtainementDate(Instant.now())
                .description("Provisional Certification Request")
                .build();
                
        return certificationRepository.save(cert);
    }

    public Mono<Contact> addContact(UUID userId, Contact contact, String jwtToken) {
        // Strip "Bearer " if present, though adapters usually handle raw or bearer
        String token = jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;
        
        return businessActorRepository.findByUserId(userId, token)
                .switchIfEmpty(Mono.error(new RuntimeException("Business Actor not found for user: " + userId)))
                .flatMap(actor -> organisationRepository.findByActorId(actor.getId(), token))
                .switchIfEmpty(Mono.error(new RuntimeException("Organisation not found for actor")))
                .flatMap(org -> {
                    contact.setContactableId(org.getId());
                    contact.setContactableType("ORGANISATION"); 
                    return contactRepository.save(contact, token);
                });
    }

    public Flux<Contact> getContacts(UUID userId, String jwtToken) {
        String token = jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;

        return businessActorRepository.findByUserId(userId, token)
                .flatMap(actor -> organisationRepository.findByActorId(actor.getId(), token))
                .flatMapMany(org -> contactRepository.findAllByContactableId(org.getId(), token));
    }
}
