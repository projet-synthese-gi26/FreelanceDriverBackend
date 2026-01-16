package com.yowyob.template.infrastructure.adapters.outbound.persistence;

import com.yowyob.template.domain.model.Address;
import com.yowyob.template.domain.ports.out.AddressRepositoryPort;
import com.yowyob.template.infrastructure.adapters.outbound.persistence.repository.AddressR2dbcRepository;
import com.yowyob.template.infrastructure.mappers.AddressMapper;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AddressR2dbcAdapter implements AddressRepositoryPort {

    private final AddressR2dbcRepository repository;
    private final AddressMapper mapper;

    @Override
    public Mono<Address> save(Address address) {
        return repository.save(mapper.toEntity(address))
                .map(mapper::toDomain);
    }
    @Override
    public Mono<Address> findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Address> findAll() {
        return repository.findAll()
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteAll() {
        return repository.deleteAll();
    }


    @Override
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id);
    }
   

}