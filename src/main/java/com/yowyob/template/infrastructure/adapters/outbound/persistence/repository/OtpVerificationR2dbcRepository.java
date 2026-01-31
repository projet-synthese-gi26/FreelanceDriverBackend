package com.yowyob.template.infrastructure.adapters.outbound.persistence.repository;

import com.yowyob.template.infrastructure.adapters.outbound.persistence.entity.OtpVerificationEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface OtpVerificationR2dbcRepository extends ReactiveCrudRepository<OtpVerificationEntity, UUID> {
    Mono<OtpVerificationEntity> findByEmail(String email);
}
