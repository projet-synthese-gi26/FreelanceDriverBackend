package com.yowyob.template.domain.ports.out;

import java.util.UUID;

import com.yowyob.template.domain.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepositoryPort {
    Mono<Product> save(Product product);
    Mono<Product> findById(UUID id);
    Flux<Product> findAll();
    Mono<Void> deleteById(UUID id);
    Mono<Void> deleteAll();
}