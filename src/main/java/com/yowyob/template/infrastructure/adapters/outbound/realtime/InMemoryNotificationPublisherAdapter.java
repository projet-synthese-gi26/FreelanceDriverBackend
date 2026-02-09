package com.yowyob.template.infrastructure.adapters.outbound.realtime;

import com.yowyob.template.domain.model.AppNotification;
import com.yowyob.template.domain.ports.out.NotificationPublisherPort;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryNotificationPublisherAdapter implements NotificationPublisherPort {

    private final Map<UUID, Sinks.Many<AppNotification>> sinks = new ConcurrentHashMap<>();

    private Sinks.Many<AppNotification> sink(UUID userId) {
        return sinks.computeIfAbsent(userId, id -> Sinks.many().multicast().onBackpressureBuffer());
    }

    @Override
    public Mono<Void> publish(UUID userId, AppNotification notification) {
        sink(userId).tryEmitNext(notification);
        return Mono.empty();
    }

    @Override
    public Flux<AppNotification> subscribe(UUID userId) {
        return sink(userId).asFlux();
    }
}
