package com.yowyob.template.application.service;

import com.yowyob.template.domain.model.Contact;
import com.yowyob.template.domain.ports.in.CreateContactUseCase;
import com.yowyob.template.domain.ports.in.GetContactsUseCase;
import com.yowyob.template.domain.ports.out.ContactRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactService implements CreateContactUseCase, GetContactsUseCase {
    private final ContactRepositoryPort repository;

    @Override
    public Mono<Contact> createContact(Contact contact) {
        // Ajoute les timestamps si absents
        java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
        Contact toSave = Contact.builder()
                .id(contact.getId())
                .contactableId(contact.getContactableId())
                .contactableType(contact.getContactableType())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .title(contact.getTitle())
                .isEmailVerified(contact.getIsEmailVerified())
                .isPhoneNumberVerified(contact.getIsPhoneNumberVerified())
                .isFavorite(contact.getIsFavorite())
                .phoneNumber(contact.getPhoneNumber())
                .secondaryPhoneNumber(contact.getSecondaryPhoneNumber())
                .faxNumber(contact.getFaxNumber())
                .email(contact.getEmail())
                .secondaryEmail(contact.getSecondaryEmail())
                .emailVerifiedAt(contact.getEmailVerifiedAt())
                .phoneVerifiedAt(contact.getPhoneVerifiedAt())
                .createdAt(contact.getCreatedAt() != null ? contact.getCreatedAt() : now)
                .updatedAt(contact.getUpdatedAt() != null ? contact.getUpdatedAt() : now)
                .deletedAt(contact.getDeletedAt())
                .build();
        return repository.save(toSave);
    }

    @Override
    public Mono<Contact> getContactById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Flux<Contact> getAllContacts() {
        return repository.findAll();
    }

    @Override
    public Mono<Contact> updateContact(UUID id, Contact contact) {
        return repository.findById(id)
                .flatMap(existing -> {
                    Contact updated = Contact.builder()
                            .id(id)
                            .contactableId(contact.getContactableId() != null ? contact.getContactableId() : existing.getContactableId())
                            .contactableType(contact.getContactableType() != null ? contact.getContactableType() : existing.getContactableType())
                            .firstName(contact.getFirstName() != null ? contact.getFirstName() : existing.getFirstName())
                            .lastName(contact.getLastName() != null ? contact.getLastName() : existing.getLastName())
                            .title(contact.getTitle() != null ? contact.getTitle() : existing.getTitle())
                            .isEmailVerified(contact.getIsEmailVerified() != null ? contact.getIsEmailVerified() : existing.getIsEmailVerified())
                            .isPhoneNumberVerified(contact.getIsPhoneNumberVerified() != null ? contact.getIsPhoneNumberVerified()
                                    : existing.getIsPhoneNumberVerified())
                            .isFavorite(contact.getIsFavorite() != null ? contact.getIsFavorite() : existing.getIsFavorite())
                            .phoneNumber(contact.getPhoneNumber() != null ? contact.getPhoneNumber() : existing.getPhoneNumber())
                            .secondaryPhoneNumber(contact.getSecondaryPhoneNumber() != null ? contact.getSecondaryPhoneNumber()
                                    : existing.getSecondaryPhoneNumber())
                            .faxNumber(contact.getFaxNumber() != null ? contact.getFaxNumber() : existing.getFaxNumber())
                            .email(contact.getEmail() != null ? contact.getEmail() : existing.getEmail())
                            .secondaryEmail(contact.getSecondaryEmail() != null ? contact.getSecondaryEmail() : existing.getSecondaryEmail())
                            .emailVerifiedAt(contact.getEmailVerifiedAt() != null ? contact.getEmailVerifiedAt() : existing.getEmailVerifiedAt())
                            .phoneVerifiedAt(contact.getPhoneVerifiedAt() != null ? contact.getPhoneVerifiedAt() : existing.getPhoneVerifiedAt())
                            .createdAt(existing.getCreatedAt())
                            .updatedAt(new java.sql.Timestamp(System.currentTimeMillis()))
                            .deletedAt(contact.getDeletedAt() != null ? contact.getDeletedAt() : existing.getDeletedAt())
                            .build();
                    return repository.save(updated);
                });
    }

    @Override
    public Flux<Contact> getContacts(UUID contactableId) {
        return repository.findAllByContactableId(contactableId);
    }

    @Override
    public Flux<Contact> getContacts(UUID contactableId, String jwtToken) {
        String token = jwtToken != null && jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;
        return repository.findAllByContactableId(contactableId, token);
    }
    
    @Override
    public Mono<Void> deleteContact(UUID id) {
        return repository.deleteById(id);
    }
    
    public Mono<Void> deleteContact(UUID id, String jwtToken) {
        String token = jwtToken != null && jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;
        return repository.deleteById(id, token);
    }
}
