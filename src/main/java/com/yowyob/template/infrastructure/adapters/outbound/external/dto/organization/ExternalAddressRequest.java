package com.yowyob.template.infrastructure.adapters.outbound.external.dto.organization;

import java.util.UUID;

public record ExternalAddressRequest(
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
    String informalDescription,
    Boolean isDefault,
    Double latitude,
    Double longitude
) {}
