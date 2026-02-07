package com.yowyob.template.infrastructure.adapters.outbound.external;

import com.yowyob.template.domain.ports.out.NotificationPort;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.notification.NotificationCreatePullRequest;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.notification.NotificationCreatePullResponse;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.notification.NotificationSendRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
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
    public Mono<NotificationCreatePullResponse> createPull(NotificationCreatePullRequest request) {
        return client().post()
                .uri("/api/v1/notifications")
                .header("X-Service-Token", serviceToken)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(NotificationCreatePullResponse.class);
    }

    @Override
    public Mono<Void> send(NotificationSendRequest request) {
        return client().post()
                .uri("/api/v1/notifications/send")
                .header("X-Service-Token", serviceToken)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
