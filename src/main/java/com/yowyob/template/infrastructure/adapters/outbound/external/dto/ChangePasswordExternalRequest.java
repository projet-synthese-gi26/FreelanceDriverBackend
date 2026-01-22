package com.yowyob.template.infrastructure.adapters.outbound.external.dto;

public record ChangePasswordExternalRequest(String currentPassword, String newPassword) {}
