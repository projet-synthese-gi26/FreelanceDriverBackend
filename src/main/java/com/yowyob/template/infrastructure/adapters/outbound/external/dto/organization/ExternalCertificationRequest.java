package com.yowyob.template.infrastructure.adapters.outbound.external.dto.organization;

import java.time.Instant;
import java.util.UUID;

public record ExternalCertificationRequest(
    UUID organizationId,
    String name,
    String type,
    String description,
    Instant obtainementDate
) {}
