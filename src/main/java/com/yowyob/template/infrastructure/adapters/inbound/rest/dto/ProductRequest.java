package com.yowyob.template.infrastructure.adapters.inbound.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ProductRequest(
    @NotNull UUID organizationId,
    @NotBlank String name,
    String description,
    Boolean isActive,
    String standardPrice,
    String departureLocation,
    String arrivalLocation,
    OffsetDateTime startDate,
    LocalTime startTime,
    OffsetDateTime endDate,
    LocalTime endTime,
    String baggageInfo,
    Boolean isNegotiable,
    String paymentMethod,
    String title,
    String status,
    List<String> productUrls,
    String regularAmount,
    @Positive BigDecimal discountPercentage,
    @Positive BigDecimal discountedAmount,
    List<String> metadata
) {}