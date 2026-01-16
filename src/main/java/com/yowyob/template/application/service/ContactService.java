package com.yowyob.template.application.service;

import com.yowyob.template.domain.model.Contact;
import com.yowyob.template.domain.ports.in.CreateContactUseCase;
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
public class ContactService implements CreateContactUseCase {
    private final ContactRepositoryPort repository;

    @Override
    public Mono<Contact> createContact(Contact contact) {
        // Ajoute les timestamps si absents
        java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
        Contact toSave = new Contact(
                contact.id(),
                contact.contactableId(),
                contact.contactableType(),
                contact.firstName(),
                contact.lastName(),
                contact.title(),
                contact.isEmailVerified(),
                contact.isPhoneNumberVerified(),
                contact.isFavorite(),
                contact.phoneNumber(),
                contact.secondaryPhoneNumber(),
                contact.faxNumber(),
                contact.email(),
                contact.secondaryEmail(),
                contact.emailVerifiedAt(),
                contact.phoneVerifiedAt(),
                contact.createdAt() != null ? contact.createdAt() : now,
                contact.updatedAt() != null ? contact.updatedAt() : now,
                contact.deletedAt());
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
                    Contact updated = new Contact(
                            id,
                            contact.contactableId() != null ? contact.contactableId() : existing.contactableId(),
                            contact.contactableType() != null ? contact.contactableType() : existing.contactableType(),
                            contact.firstName() != null ? contact.firstName() : existing.firstName(),
                            contact.lastName() != null ? contact.lastName() : existing.lastName(),
                            contact.title() != null ? contact.title() : existing.title(),
                            contact.isEmailVerified() != null ? contact.isEmailVerified() : existing.isEmailVerified(),
                            contact.isPhoneNumberVerified() != null ? contact.isPhoneNumberVerified()
                                    : existing.isPhoneNumberVerified(),
                            contact.isFavorite() != null ? contact.isFavorite() : existing.isFavorite(),
                            contact.phoneNumber() != null ? contact.phoneNumber() : existing.phoneNumber(),
                            contact.secondaryPhoneNumber() != null ? contact.secondaryPhoneNumber()
                                    : existing.secondaryPhoneNumber(),
                            contact.faxNumber() != null ? contact.faxNumber() : existing.faxNumber(),
                            contact.email() != null ? contact.email() : existing.email(),
                            contact.secondaryEmail() != null ? contact.secondaryEmail() : existing.secondaryEmail(),
                            contact.emailVerifiedAt() != null ? contact.emailVerifiedAt() : existing.emailVerifiedAt(),
                            contact.phoneVerifiedAt() != null ? contact.phoneVerifiedAt() : existing.phoneVerifiedAt(),
                            contact.createdAt() != null ? contact.createdAt() : existing.createdAt(),
                            contact.updatedAt() != null ? contact.updatedAt() : existing.updatedAt(),
                            contact.deletedAt() != null ? contact.deletedAt() : existing.deletedAt());
                    return repository.save(updated);
                });
    }

    @Override
    public Mono<Void> deleteContact(UUID id) {
        return repository.deleteById(id);
    }
}
