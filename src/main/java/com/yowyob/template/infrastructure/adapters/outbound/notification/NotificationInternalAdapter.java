package com.yowyob.template.infrastructure.adapters.outbound.notification;

import com.yowyob.template.domain.model.AppNotification;
import com.yowyob.template.domain.model.NotificationChannel;
import com.yowyob.template.domain.ports.out.AppNotificationRepositoryPort;
import com.yowyob.template.domain.ports.out.NotificationPort;
import com.yowyob.template.domain.ports.out.NotificationPublisherPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Component
@Primary
@RequiredArgsConstructor
public class NotificationInternalAdapter implements NotificationPort {

    private final AppNotificationRepositoryPort repository;
    private final NotificationPublisherPort publisher;

    @Override
    public Mono<AppNotification> notify(UUID userId,
                                        NotificationChannel channel,
                                        String type,
                                        String title,
                                        String body,
                                        Map<String, Object> data) {
        if (userId == null) {
            return Mono.error(new IllegalArgumentException("userId is required"));
        }

        var notif = AppNotification.builder()
                .userId(userId)
                .channel(channel)
                .type(type)
                .title(title)
                .body(body)
                .data(data)
                .read(false)
                .createdAt(OffsetDateTime.now())
                .build();

        return repository.save(notif)
                .flatMap(saved -> publisher.publish(userId, saved).thenReturn(saved));
    }

    @Override
    public Mono<Void> push(UUID userId,
                           String type,
                           String title,
                           String body,
                           Map<String, Object> data) {
        if (userId == null) {
            return Mono.error(new IllegalArgumentException("userId is required"));
        }

        var notif = AppNotification.builder()
                .userId(userId)
                .channel(NotificationChannel.PUSH)
                .type(type)
                .title(title)
                .body(body)
                .data(data)
                .read(true)
                .createdAt(OffsetDateTime.now())
                .build();

        return publisher.publish(userId, notif);
    }
}
