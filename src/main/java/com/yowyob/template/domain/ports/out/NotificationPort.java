package com.yowyob.template.domain.ports.out;

import com.yowyob.template.infrastructure.adapters.outbound.external.dto.notification.NotificationCreatePullRequest;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.notification.NotificationCreatePullResponse;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.notification.NotificationSendRequest;
import reactor.core.publisher.Mono;

public interface NotificationPort {
    Mono<NotificationCreatePullResponse> createPull(NotificationCreatePullRequest request);
    Mono<Void> send(NotificationSendRequest request);
}
