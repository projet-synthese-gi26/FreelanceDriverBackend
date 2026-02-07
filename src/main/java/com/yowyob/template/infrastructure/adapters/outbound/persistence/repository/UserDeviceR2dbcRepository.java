package com.yowyob.template.infrastructure.adapters.outbound.persistence.repository;

import com.yowyob.template.infrastructure.adapters.outbound.persistence.entity.UserDeviceEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserDeviceR2dbcRepository extends ReactiveCrudRepository<UserDeviceEntity, UUID> {
    Flux<UserDeviceEntity> findByUserId(UUID userId);
    Mono<UserDeviceEntity> findByFcmToken(String fcmToken);
    Mono<Void> deleteByFcmToken(String fcmToken);
}
