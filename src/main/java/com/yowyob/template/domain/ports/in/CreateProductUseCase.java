package com.yowyob.template.domain.ports.in;

import com.yowyob.template.domain.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Map;
import java.util.UUID;

public interface CreateProductUseCase {
    Mono<Product> createProduct(Product product);
    Mono<Product> createProductForOrganisation(UUID organisationId, Map<String, Object> params);
    Mono<Product> getProductById(UUID id);
    Flux<Product> getAllProducts();
    Mono<Product> updateProduct(UUID id, Product product);
    Mono<Void> deleteProduct(UUID id);
}
