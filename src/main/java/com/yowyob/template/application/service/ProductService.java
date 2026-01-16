package com.yowyob.template.application.service;

import com.yowyob.template.domain.exception.StockFullException;
import com.yowyob.template.domain.model.Product;
import com.yowyob.template.domain.ports.in.CreateProductUseCase;
import com.yowyob.template.domain.ports.out.ProductCachePort;
import com.yowyob.template.domain.ports.out.ProductEventPublisherPort;
import com.yowyob.template.domain.ports.out.ProductRepositoryPort;
import com.yowyob.template.domain.ports.out.StockClientPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService implements CreateProductUseCase {

    private final ProductRepositoryPort repository;
    private final StockClientPort stockClient;
    private final ProductCachePort cache;
    private final ProductEventPublisherPort publisher;

    @Override
    public Mono<Product> createProduct(Product product) {
        return repository.save(product);
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
                    // Toujours prendre la valeur du status du payload, même si identique
                    String newStatus = product.status();
                    Product updated = new Product(
                            id,
                            product.organizationId() != null ? product.organizationId() : existing.organizationId(),
                            product.name() != null ? product.name() : existing.name(),
                            product.description() != null ? product.description() : existing.description(),
                            product.isActive() != null ? product.isActive() : existing.isActive(),
                            product.standardPrice() != null ? product.standardPrice() : existing.standardPrice(),
                            product.departureLocation() != null ? product.departureLocation()
                                    : existing.departureLocation(),
                            product.arrivalLocation() != null ? product.arrivalLocation() : existing.arrivalLocation(),
                            product.startDate() != null ? product.startDate() : existing.startDate(),
                            product.startTime() != null ? product.startTime() : existing.startTime(),
                            product.endDate() != null ? product.endDate() : existing.endDate(),
                            product.endTime() != null ? product.endTime() : existing.endTime(),
                            product.baggageInfo() != null ? product.baggageInfo() : existing.baggageInfo(),
                            product.isNegotiable() != null ? product.isNegotiable() : existing.isNegotiable(),
                            product.paymentMethod() != null ? product.paymentMethod() : existing.paymentMethod(),
                            product.title() != null ? product.title() : existing.title(),
                            newStatus != null ? newStatus : existing.status(),
                            product.createdAt() != null ? product.createdAt() : existing.createdAt(),
                            product.updatedAt() != null ? product.updatedAt() : existing.updatedAt(),
                            product.productUrls() != null ? product.productUrls() : existing.productUrls(),
                            product.regularAmount() != null ? product.regularAmount() : existing.regularAmount(),
                            product.discountPercentage() != null ? product.discountPercentage()
                                    : existing.discountPercentage(),
                            product.discountedAmount() != null ? product.discountedAmount()
                                    : existing.discountedAmount(),
                            product.metadata() != null ? product.metadata() : existing.metadata());
                    return repository.save(updated);
                });
    }

    @Override
    public Mono<Void> deleteProduct(UUID id) {
        return repository.deleteById(id);
    }
}