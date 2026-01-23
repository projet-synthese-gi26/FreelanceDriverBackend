package com.yowyob.template.domain.ports.out;

import com.yowyob.template.domain.model.Address;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AddressRepositoryPort {
    Mono<Address> save(Address address);
    Mono<Address> save(Address address, String jwtToken);
    Mono<Address> update(Address address, String jwtToken);
    Mono<Address> findById(UUID id);
    Flux<Address> findAll();
    Flux<Address> findAllByAddressableId(UUID addressableId, String jwtToken);
    Mono<Void> deleteById(UUID id);
    Mono<Void> deleteById(UUID id, String jwtToken);
    Mono<Void> deleteAll();
    
}
