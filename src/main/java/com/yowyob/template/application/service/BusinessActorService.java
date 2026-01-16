package com.yowyob.template.application.service;

import com.yowyob.template.domain.model.BusinessActor;
import com.yowyob.template.domain.ports.in.createBusinessActorUseCase;
import com.yowyob.template.domain.ports.out.BusinessActorRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessActorService implements createBusinessActorUseCase {
    private final BusinessActorRepositoryPort repository;

    @Override
    public Mono<BusinessActor> createBusinessActor(BusinessActor businessActor) {
        java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
        BusinessActor toSave = new BusinessActor(
                businessActor.id(),
                businessActor.userId(),
                businessActor.name(),
                businessActor.phoneNumber(),
                businessActor.emailAddress());
        // Si le modèle BusinessActor a des champs createdAt/updatedAt, ajoute-les ici
        return repository.save(toSave);
    }

    @Override
    public Mono<BusinessActor> getBusinessActorById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Flux<BusinessActor> getAllBusinessActors() {
        return repository.findAll();
    }

    @Override
    public Mono<BusinessActor> updateBusinessActor(UUID id, BusinessActor businessActor) {
        return repository.findById(id)
                .flatMap(existing -> {
                    BusinessActor updated = new BusinessActor(
                            id,
                            businessActor.userId() != null ? businessActor.userId() : existing.userId(),
                            businessActor.name() != null ? businessActor.name() : existing.name(),
                            businessActor.phoneNumber() != null ? businessActor.phoneNumber() : existing.phoneNumber(),
                            businessActor.emailAddress() != null ? businessActor.emailAddress()
                                    : existing.emailAddress());
                    return repository.save(updated);
                });
    }

    @Override
    public Mono<Void> deleteBusinessActor(UUID id) {
        return repository.deleteById(id);
    }
}
