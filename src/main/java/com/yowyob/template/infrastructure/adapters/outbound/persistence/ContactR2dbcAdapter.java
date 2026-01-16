package com.yowyob.template.infrastructure.adapters.outbound.persistence;

import com.yowyob.template.domain.model.Contact;
import com.yowyob.template.domain.ports.out.ContactRepositoryPort;
import com.yowyob.template.infrastructure.adapters.outbound.persistence.repository.ContactR2dbcRepository;
import com.yowyob.template.infrastructure.mappers.ContactMapper;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ContactR2dbcAdapter implements ContactRepositoryPort {

    private final ContactR2dbcRepository repository;
    private final ContactMapper mapper;

    @Override
    public Mono<Contact> save(Contact contact) {
        return repository.save(mapper.toEntity(contact))
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Contact> findAll() {
        return repository.findAll()
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteAll() {
        return repository.deleteAll();
    }

    @Override
    public Mono<Contact> findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id);
    }
}