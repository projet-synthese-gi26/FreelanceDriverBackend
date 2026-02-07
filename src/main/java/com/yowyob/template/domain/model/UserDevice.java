package com.yowyob.template.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDevice {
    private UUID id;
    private UUID userId;
    private String fcmToken;
    private DevicePlatform platform;
    private OffsetDateTime updatedAt;
}
