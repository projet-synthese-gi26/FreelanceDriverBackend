package com.yowyob.template.infrastructure.adapters.outbound.external.dto.organization;

import java.util.UUID;

public record ExternalContactRequest(
    UUID contactableId,
    String contactableType,
    String title,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    String secondaryPhoneNumber,
    String faxNumber,
    String secondaryEmail,
    Boolean isFavorite,
    Boolean isEmailVerified,
    Boolean isPhoneNumberVerified
) {}
