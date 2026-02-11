package com.yowyob.template.infrastructure.adapters.outbound.external.dto.notification;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record NotificationSendRequest(
        String notificationType,
        UUID templateId,
        List<String> to,
        Map<String, Object> data
) {}
