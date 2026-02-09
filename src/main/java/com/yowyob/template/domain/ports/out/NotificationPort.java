package com.yowyob.template.domain.ports.out;

import com.yowyob.template.domain.model.AppNotification;
import com.yowyob.template.domain.model.NotificationChannel;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

public interface NotificationPort {
    Mono<AppNotification> notify(UUID userId,
                                NotificationChannel channel,
                                String type,
                                String title,
                                String body,
                                Map<String, Object> data);

    Mono<Void> push(UUID userId,
                   String type,
                   String title,
                   String body,
                   Map<String, Object> data);
}
