package com.yowyob.template.domain.ports.out;

import com.yowyob.template.domain.model.AppNotification;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface NotificationPublisherPort {
    Mono<Void> publish(UUID userId, AppNotification notification);
    Flux<AppNotification> subscribe(UUID userId);
}
