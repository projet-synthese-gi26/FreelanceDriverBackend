package com.yowyob.template.infrastructure.adapters.inbound.rest.dto;

import java.time.Instant;
import java.util.UUID;

public record CertificationRequest(
    UUID organizationId,
    String name,
    String type,
    String description,
    Instant obtainementDate
) {}
