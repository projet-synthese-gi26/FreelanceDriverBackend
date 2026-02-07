package com.yowyob.template.infrastructure.adapters.outbound.external.dto.notification;

import java.util.Map;
import java.util.UUID;

public record NotificationCreatePullRequest(
        String notificationType,
        UUID templateId,
        UUID userId,
        Map<String, Object> data
) {}
