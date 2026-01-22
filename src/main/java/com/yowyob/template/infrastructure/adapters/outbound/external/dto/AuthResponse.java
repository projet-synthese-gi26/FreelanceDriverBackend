package com.yowyob.template.infrastructure.adapters.outbound.external.dto;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    TraMaSysUserResponse user
) {}
