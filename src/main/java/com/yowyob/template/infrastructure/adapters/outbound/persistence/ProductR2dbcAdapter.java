package com.yowyob.template.infrastructure.adapters.outbound.persistence;

import com.yowyob.template.domain.model.Product;
import com.yowyob.template.domain.ports.out.ProductRepositoryPort;
import com.yowyob.template.infrastructure.adapters.outbound.persistence.repository.ProductR2dbcRepository;
import com.yowyob.template.infrastructure.mappers.ProductMapper;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductR2dbcAdapter implements ProductRepositoryPort {

    private final ProductR2dbcRepository repository;
    private final ProductMapper mapper;

    @Override
    public Mono<Product> save(Product product) {
        return repository.save(mapper.toEntity(product))
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Product> findById(UUID id){
        return repository.findById(id).
               map(mapper::toDomain);
    }

    @Override
    public Mono<Product> findByIdAndProductType(UUID id, String productType) {
        return repository.findByIdAndProductType(id, productType)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Product> findAll(){
        return repository.findAll().
               map(mapper::toDomain);
    }

    @Override
    public Flux<Product> findByProductTypeAndClientId(String productType, UUID clientId) {
        return repository.findByProductTypeAndClientId(productType, clientId)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(UUID id){
        return repository.deleteById(id);
    }

    @Override
    public Mono<Void> deleteAll(){
        return repository.deleteAll();
    }
    
}