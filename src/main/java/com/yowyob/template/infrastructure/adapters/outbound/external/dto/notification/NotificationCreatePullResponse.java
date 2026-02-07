package com.yowyob.template.infrastructure.adapters.outbound.external.dto.notification;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public record NotificationCreatePullResponse(
        Integer notificationId,
        UUID userId,
        UUID templateId,
        UUID serviceAppId,
        String notificationType,
        String status,
        OffsetDateTime createdAt,
        Map<String, Object> data
) {}
