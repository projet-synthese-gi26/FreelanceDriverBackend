package com.yowyob.template.infrastructure.adapters.inbound.rest.dto;


public record AddressRequest(
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
        Double longitude) {
}