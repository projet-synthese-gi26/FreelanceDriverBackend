package com.yowyob.template.infrastructure.adapters.inbound.rest.dto;

import java.sql.Timestamp;
import java.util.UUID;

public record AddressResponse(
    UUID id,
    String addressableType,
    String type,
    String addressLine1,
    String addressLine2,
    String city,
    String state,
    String locality,
    String zipCode,
    String postalCode,
    String poBox,
    Boolean isDefault,
    String neighborhood,
    String informalDescription,
    Double latitude,
    Double longitude,
    Timestamp createdAt,
    Timestamp updatedAt,
    Timestamp deletedAt
) {}