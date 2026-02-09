package com.yowyob.template.infrastructure.mappers;

import com.yowyob.template.domain.model.AppNotification;
import com.yowyob.template.domain.model.NotificationChannel;
import com.yowyob.template.infrastructure.adapters.outbound.persistence.entity.AppNotificationEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AppNotificationMapper {
    private final ObjectMapper objectMapper;

    public AppNotificationEntity toEntity(AppNotification domain) {
        if (domain == null) {
            return null;
        }

        String dataJson = null;
        try {
            if (domain.getData() != null) {
                dataJson = objectMapper.writeValueAsString(domain.getData());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid notification data", e);
        }

        return AppNotificationEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .channel(domain.getChannel() != null ? domain.getChannel().name() : null)
                .type(domain.getType())
                .title(domain.getTitle())
                .body(domain.getBody())
                .data(dataJson)
                .isRead(domain.isRead())
                .createdAt(domain.getCreatedAt() != null ? domain.getCreatedAt() : OffsetDateTime.now())
                .build();
    }

    public AppNotification toDomain(AppNotificationEntity entity) {
        if (entity == null) {
            return null;
        }

        Map<String, Object> data = null;
        try {
            if (entity.getData() != null && !entity.getData().isBlank()) {
                data = objectMapper.readValue(entity.getData(), new TypeReference<>() {});
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid notification data", e);
        }

        return AppNotification.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .channel(entity.getChannel() != null ? NotificationChannel.valueOf(entity.getChannel()) : null)
                .type(entity.getType())
                .title(entity.getTitle())
                .body(entity.getBody())
                .data(data)
                .read(Boolean.TRUE.equals(entity.getIsRead()))
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
