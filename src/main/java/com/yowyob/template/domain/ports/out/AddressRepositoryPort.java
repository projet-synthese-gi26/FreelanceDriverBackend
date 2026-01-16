package com.yowyob.template.domain.ports.out;

import com.yowyob.template.domain.model.Address;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AddressRepositoryPort {
    Mono<Address> save(Address address);
    Mono<Address> findById(UUID id);
    Flux<Address> findAll();
    Mono<Void> deleteById(UUID id);
    Mono<Void> deleteAll();
    
}
