package com.yowyob.template.infrastructure.adapters.outbound.external;

import com.yowyob.template.domain.model.AppNotification;
import com.yowyob.template.domain.model.NotificationChannel;
import com.yowyob.template.domain.ports.out.NotificationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class NotificationExternalAdapter implements NotificationPort {

    private final WebClient.Builder webClientBuilder;

    @Value("${application.external.notification-service-url:https://notification-service.pynfi.com}")
    private String notificationServiceUrl;

    @Value("${application.external.notification-service-token:}")
    private String serviceToken;

    private WebClient client() {
        return webClientBuilder.baseUrl(notificationServiceUrl).build();
    }

    @Override
    public Mono<AppNotification> notify(UUID userId,
                                        NotificationChannel channel,
                                        String type,
                                        String title,
                                        String body,
                                        Map<String, Object> data) {
        log.warn("External notification provider disabled/unavailable; skipping notify(type={})", type);
        return Mono.empty();
    }

    @Override
    public Mono<Void> push(UUID userId,
                           String type,
                           String title,
                           String body,
                           Map<String, Object> data) {
        log.warn("External notification provider disabled/unavailable; skipping push(type={})", type);
        return Mono.empty();
    }
}
