package com.yowyob.template.application.service;

import com.yowyob.template.domain.model.Product;
import com.yowyob.template.domain.ports.in.CreateProductUseCase;
import com.yowyob.template.domain.ports.out.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService implements CreateProductUseCase {
    private final ProductRepositoryPort repository;
    private final OrganisationRepositoryPort organisationRepository;
    private final StockClientPort stockClient;
    private final ProductCachePort cache;
    private final ProductEventPublisherPort publisher;

    @Override
    public Mono<Product> createProduct(Product product) {
        product.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        return repository.save(product)
                .flatMap(saved -> publisher.publishProductCreated(saved).thenReturn(saved));
    }

    @Override
    public Mono<Product> createProductForOrganisation(UUID organisationId, Map<String, Object> params) {
        return organisationRepository.findById(organisationId)
                .flatMap(org -> {
                    Product product = org.createProduct(params);
                    product.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                    return repository.save(product)
                            .flatMap(saved -> publisher.publishProductCreated(saved).thenReturn(saved));
                });
    }

    @Override
    public Mono<Product> getProductById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Flux<Product> getAllProducts() {
        return repository.findAll();
    }

    @Override
    public Mono<Product> updateProduct(UUID id, Product product) {
        return repository.findById(id)
                .flatMap(existing -> {
                    existing.setTitle(product.getTitle() != null ? product.getTitle() : existing.getTitle());
                    existing.setDescription(product.getDescription() != null ? product.getDescription() : existing.getDescription());
                    existing.setStatus(product.getStatus() != null ? product.getStatus() : existing.getStatus());
                    existing.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                    return repository.save(existing)
                            .flatMap(saved -> publisher.publishProductUpdated(saved).thenReturn(saved));
                });
    }

    @Override
    public Mono<Void> deleteProduct(UUID id) {
        return repository.findById(id)
                .flatMap(product -> repository.deleteById(id)
                        .then(publisher.publishProductDeleted(product)));
    }
}
