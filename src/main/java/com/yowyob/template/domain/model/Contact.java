package com.yowyob.template.domain.model;

import java.sql.Timestamp;
import java.util.UUID;

public record Contact(
    UUID id,
    UUID contactableId,
    String contactableType,
    String firstName,
    String lastName,
    String title,
    Boolean isEmailVerified,
    Boolean isPhoneNumberVerified,
    Boolean isFavorite,
    String phoneNumber,
    String secondaryPhoneNumber,
    String faxNumber,
    String email,
    String secondaryEmail,
    Timestamp emailVerifiedAt,
    Timestamp phoneVerifiedAt,
    Timestamp createdAt,
    Timestamp updatedAt,
    Timestamp deletedAt
) {}