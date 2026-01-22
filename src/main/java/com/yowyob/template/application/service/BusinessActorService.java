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
        return repository.save(businessActor);
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
                    existing.setDisplayName(businessActor.getDisplayName() != null ? businessActor.getDisplayName() : existing.getDisplayName());
                    existing.setPhoneNumber(businessActor.getPhoneNumber() != null ? businessActor.getPhoneNumber() : existing.getPhoneNumber());
                    existing.setEmailAddress(businessActor.getEmailAddress() != null ? businessActor.getEmailAddress() : existing.getEmailAddress());
                    existing.setAvatarUrl(businessActor.getAvatarUrl() != null ? businessActor.getAvatarUrl() : existing.getAvatarUrl());
                    return repository.save(existing);
                });
    }

    @Override
    public Mono<Void> deleteBusinessActor(UUID id) {
        return repository.deleteById(id);
    }
}
