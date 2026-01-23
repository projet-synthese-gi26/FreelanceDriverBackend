package com.yowyob.template.domain.ports.out;

import java.util.UUID;

import com.yowyob.template.domain.model.Contact;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ContactRepositoryPort {

    Mono<Contact> save(Contact contact);
    Mono<Contact> save(Contact contact, String jwtToken);
    Mono<Contact> update(Contact contact, String jwtToken);

    Mono<Contact> findById(UUID id);

    Flux<Contact> findAll();
    
    Flux<Contact> findAllByContactableId(UUID contactableId);

    Flux<Contact> findAllByContactableId(UUID contactableId, String jwtToken);

    Mono<Void> deleteById(UUID id);
    Mono<Void> deleteById(UUID id, String jwtToken);

    Mono<Void> deleteAll();
    
}
