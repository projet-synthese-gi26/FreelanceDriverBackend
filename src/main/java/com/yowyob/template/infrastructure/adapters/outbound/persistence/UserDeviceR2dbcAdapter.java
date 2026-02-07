package com.yowyob.template.infrastructure.adapters.outbound.persistence;

import com.yowyob.template.domain.model.UserDevice;
import com.yowyob.template.domain.ports.out.UserDeviceRepositoryPort;
import com.yowyob.template.infrastructure.adapters.outbound.persistence.repository.UserDeviceR2dbcRepository;
import com.yowyob.template.infrastructure.mappers.UserDeviceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserDeviceR2dbcAdapter implements UserDeviceRepositoryPort {

    private final UserDeviceR2dbcRepository repository;
    private final UserDeviceMapper mapper;

    @Override
    public Mono<UserDevice> upsert(UserDevice device) {
        if (device == null || device.getFcmToken() == null || device.getFcmToken().isBlank()) {
            return Mono.error(new IllegalArgumentException("fcmToken est obligatoire"));
        }
        if (device.getUserId() == null) {
            return Mono.error(new IllegalArgumentException("userId est obligatoire"));
        }

        return repository.findByFcmToken(device.getFcmToken())
                .defaultIfEmpty(new com.yowyob.template.infrastructure.adapters.outbound.persistence.entity.UserDeviceEntity())
                .flatMap(existing -> {
                    UUID id = existing.getId() != null ? existing.getId() : device.getId();
                    var entity = mapper.toEntity(device);
                    entity.setId(id);
                    entity.setUpdatedAt(OffsetDateTime.now());
                    return repository.save(entity);
                })
                .map(mapper::toDomain);
    }

    @Override
    public Flux<UserDevice> findByUserId(UUID userId) {
        return repository.findByUserId(userId)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteByToken(String fcmToken) {
        return repository.deleteByFcmToken(fcmToken);
    }
}
