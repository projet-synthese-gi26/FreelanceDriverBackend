package com.yowyob.template.infrastructure.adapters.outbound.persistence.entity;

import com.yowyob.template.domain.model.DevicePlatform;
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
@Table("user_devices")
public class UserDeviceEntity {
    @Id
    private UUID id;
    private UUID userId;
    private String fcmToken;
    private DevicePlatform platform;
    private OffsetDateTime updatedAt;
}
