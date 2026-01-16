package com.yowyob.template.domain.ports.out;

import java.util.UUID;

import com.yowyob.template.domain.model.Contact;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ContactRepositoryPort {

    Mono<Contact> save(Contact contact);

    Mono<Contact> findById(UUID id);

    Flux<Contact> findAll();

    Mono<Void> deleteById(UUID id);

    Mono<Void> deleteAll();
    
}
