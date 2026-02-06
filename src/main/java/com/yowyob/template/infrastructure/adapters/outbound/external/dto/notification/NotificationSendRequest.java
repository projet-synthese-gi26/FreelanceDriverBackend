package com.yowyob.template.infrastructure.adapters.outbound.external.dto.notification;

import lombok.Builder;
import java.util.List;
import java.util.Map;

@Builder
public record NotificationSendRequest(
    String notificationType,
    Integer templateId,
    List<String> to,
    Map<String, Object> data
) {}