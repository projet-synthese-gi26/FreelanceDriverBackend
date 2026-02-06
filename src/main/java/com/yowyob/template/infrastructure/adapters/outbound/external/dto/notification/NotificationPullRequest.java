package com.yowyob.template.infrastructure.adapters.outbound.external.dto.notification;

import lombok.Builder;
import java.util.Map;
import java.util.UUID;

@Builder
public record NotificationPullRequest(
    String notificationType,
    Integer templateId,
    UUID userId, // <--- Différence clé avec le Send
    Map<String, Object> data
) {}