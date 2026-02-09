package com.yowyob.template.infrastructure.adapters.outbound.persistence;

import com.yowyob.template.domain.model.AppNotification;
import com.yowyob.template.domain.ports.out.AppNotificationRepositoryPort;
import com.yowyob.template.infrastructure.adapters.outbound.persistence.repository.AppNotificationR2dbcRepository;
import com.yowyob.template.infrastructure.mappers.AppNotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AppNotificationR2dbcAdapter implements AppNotificationRepositoryPort {

    private final AppNotificationR2dbcRepository repository;
    private final AppNotificationMapper mapper;

    @Override
    public Mono<AppNotification> save(AppNotification notification) {
        var entity = mapper.toEntity(notification);
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(OffsetDateTime.now());
        }
        if (entity.getIsRead() == null) {
            entity.setIsRead(false);
        }
        return repository.save(entity).map(mapper::toDomain);
    }

    @Override
    public Flux<AppNotification> findByUserId(UUID userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId).map(mapper::toDomain);
    }

    @Override
    public Mono<AppNotification> markRead(UUID userId, UUID notificationId, boolean read) {
        return repository.findById(notificationId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("notification not found")))
                .flatMap(existing -> {
                    if (!existing.getUserId().equals(userId)) {
                        return Mono.error(new IllegalArgumentException("forbidden"));
                    }
                    existing.setIsRead(read);
                    return repository.save(existing);
                })
                .map(mapper::toDomain);
    }
}
