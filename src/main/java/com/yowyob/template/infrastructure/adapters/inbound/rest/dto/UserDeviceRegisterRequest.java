package com.yowyob.template.infrastructure.adapters.inbound.rest.dto;

import com.yowyob.template.domain.model.DevicePlatform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserDeviceRegisterRequest(
        @NotBlank String fcmToken,
        @NotNull DevicePlatform platform
) {}
