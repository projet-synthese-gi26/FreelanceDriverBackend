package com.yowyob.template.infrastructure.adapters.outbound.persistence.repository;

import com.yowyob.template.infrastructure.adapters.outbound.persistence.entity.ProductEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ProductR2dbcRepository extends ReactiveCrudRepository<ProductEntity, UUID> {
    Mono<ProductEntity> findByIdAndProductType(UUID id, String productType);
    Flux<ProductEntity> findByProductTypeAndClientId(String productType, UUID clientId);

    @Query("SELECT * FROM products WHERE product_type = :productType AND status = CAST(:status AS product_status)")
    Flux<ProductEntity> findByProductTypeAndStatus(String productType, String status);
}