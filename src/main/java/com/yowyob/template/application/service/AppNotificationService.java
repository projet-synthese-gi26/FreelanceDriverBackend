package com.yowyob.template.application.service;

import com.yowyob.template.domain.model.AppNotification;
import com.yowyob.template.domain.ports.out.AppNotificationRepositoryPort;
import com.yowyob.template.domain.ports.out.NotificationPublisherPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppNotificationService {

    private final AppNotificationRepositoryPort repository;
    private final NotificationPublisherPort publisher;

    public Flux<AppNotification> myNotifications(UUID userId) {
        return repository.findByUserId(userId);
    }

    public Mono<AppNotification> markRead(UUID userId, UUID notificationId, boolean read) {
        return repository.markRead(userId, notificationId, read)
                .flatMap(updated -> publisher.publish(userId, updated).thenReturn(updated));
    }

    public Flux<AppNotification> stream(UUID userId) {
        return publisher.subscribe(userId);
    }
}
