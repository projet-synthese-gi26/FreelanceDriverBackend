package com.yowyob.template.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppNotification {
    private UUID id;
    private UUID userId;
    private NotificationChannel channel;
    private String type;
    private String title;
    private String body;
    private Map<String, Object> data;
    private boolean read;
    private OffsetDateTime createdAt;
}
