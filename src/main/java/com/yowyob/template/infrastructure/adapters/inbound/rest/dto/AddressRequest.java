package com.yowyob.template.infrastructure.adapters.inbound.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AddressRequest(
        @NotBlank String addressableType,
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
        Double longitude) {
}