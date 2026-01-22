package com.yowyob.template.infrastructure.adapters.outbound.external.dto.organization;

import java.util.UUID;
import java.time.LocalDateTime;

public record ExternalAddressResponse(
    UUID id,
    UUID addressableId,
    String addressableType,
    String type,
    String addressLine1,
    String addressLine2,
    String city,
    String state,
    String locality,
    String zipCode,
    UUID countryId,
    String poBox,
    String neighborHood,
    Boolean isDefault,
    Double latitude,
    Double longitude,
    LocalDateTime createdAt
) {}
