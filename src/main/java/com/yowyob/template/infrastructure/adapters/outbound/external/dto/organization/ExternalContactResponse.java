package com.yowyob.template.infrastructure.adapters.outbound.external.dto.organization;

import java.util.UUID;
import java.time.LocalDateTime;

public record ExternalContactResponse(
    UUID id,
    UUID contactableId,
    String contactableType,
    String firstName,
    String lastName,
    String title,
    String email,
    String phoneNumber,
    String secondaryPhoneNumber,
    String faxNumber,
    String secondaryEmail,
    Boolean isFavorite,
    Boolean isEmailVerified,
    Boolean isPhoneNumberVerified,
    LocalDateTime createdAt
) {}
