package com.yowyob.template.infrastructure.adapters.inbound.rest.dto;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.sql.Timestamp;

public record ProductResponse(
    UUID id,
    UUID organizationId,
    String name,
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
    Timestamp createdAt,
    Timestamp updatedAt,
    List<String> productUrls,
    String regularAmount,
    BigDecimal discountPercentage,
    BigDecimal discountedAmount,
    List<String> metadata
) {}