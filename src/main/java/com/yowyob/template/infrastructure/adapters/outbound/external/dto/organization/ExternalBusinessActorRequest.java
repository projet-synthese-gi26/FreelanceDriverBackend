package com.yowyob.template.infrastructure.adapters.outbound.external.dto.organization;

import java.util.List;
import java.util.UUID;

public record ExternalBusinessActorRequest(
    UUID authUserId,
    String firstName,
    String lastName,
    String email,
    Boolean isIndividual,
    Boolean isAvailable,
    String type,
    String role,
    List<String> qualifications,
    List<String> paymentMethods
) {}
