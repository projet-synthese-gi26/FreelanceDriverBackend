package com.yowyob.template.infrastructure.adapters.outbound.external.dto.organization;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

public record ExternalBusinessActorResponse(
    UUID id,
    String code,
    UUID authUserId,
    UUID organizationId,
    String firstName,
    String lastName,
    String name,
    String email,
    String type,
    String role,
    Boolean isIndividual,
    Boolean isAvailable,
    Boolean isVerified,
    Boolean isActive,
    List<String> qualifications,
    List<String> paymentMethods,
    List<ExternalAddressResponse> addresses,
    List<ExternalContactResponse> contacts,
    LocalDateTime createdAt
) {}
