package com.yowyob.template.infrastructure.adapters.outbound.external.dto;

public record AuthLoginRequest(
    String identifier,
    String password
) {}
