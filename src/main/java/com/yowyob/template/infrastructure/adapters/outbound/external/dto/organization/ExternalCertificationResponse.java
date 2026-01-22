package com.yowyob.template.infrastructure.adapters.outbound.external.dto.organization;

import java.util.UUID;
import java.time.LocalDateTime;

public record ExternalCertificationResponse(
    UUID id,
    UUID organizationId,
    String name,
    String type,
    String description,
    LocalDateTime obtainementDate,
    LocalDateTime createdAt
) {}
