package com.yowyob.template.infrastructure.adapters.outbound.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReviewR2dbcRepository extends ReactiveCrudRepository<ReviewEntity, UUID> {
    // Additional query methods if needed
}
