package com.yowyob.template.infrastructure.adapters.inbound.rest.dto;

import java.util.UUID;

public record VehicleIllustrationImageResponse(
    UUID vehicleIllustrationImageId,
    UUID vehicleId,
    String imagePath
) {}
