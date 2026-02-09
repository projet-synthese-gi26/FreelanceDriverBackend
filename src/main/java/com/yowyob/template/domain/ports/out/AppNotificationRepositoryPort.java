package com.yowyob.template.domain.ports.out;

import com.yowyob.template.domain.model.AppNotification;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AppNotificationRepositoryPort {
    Mono<AppNotification> save(AppNotification notification);
    Flux<AppNotification> findByUserId(UUID userId);
    Mono<AppNotification> markRead(UUID userId, UUID notificationId, boolean read);
}
