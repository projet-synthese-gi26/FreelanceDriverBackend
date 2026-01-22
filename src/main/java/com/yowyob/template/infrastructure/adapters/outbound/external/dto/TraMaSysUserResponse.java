package com.yowyob.template.infrastructure.adapters.outbound.external.dto;

import java.util.List;
import java.util.UUID;

public record TraMaSysUserResponse(
    String id,
    String username,
    String email,
    String phone,
    String firstName,
    String lastName,
    String service,
    UUID photoId,
    String photoUri,
    List<String> roles,
    List<String> permissions
) {}
