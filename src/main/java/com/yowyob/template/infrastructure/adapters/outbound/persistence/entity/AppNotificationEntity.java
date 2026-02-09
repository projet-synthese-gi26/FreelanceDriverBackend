package com.yowyob.template.infrastructure.adapters.outbound.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("app_notifications")
public class AppNotificationEntity {
    @Id
    private UUID id;
    private UUID userId;
    private String channel;
    private String type;
    private String title;
    private String body;
    private String data;
    private Boolean isRead;
    private OffsetDateTime createdAt;
}
