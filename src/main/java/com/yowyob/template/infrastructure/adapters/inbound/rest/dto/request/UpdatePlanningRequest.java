package com.yowyob.template.infrastructure.adapters.inbound.rest.dto.request;

import com.yowyob.template.domain.model.TripType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

public record UpdatePlanningRequest(
        String title,
        String departureLocation,
        String dropoffLocation,
        @Future
        @Schema(type = "string", format = "date-time", example = "2026-01-28T16:30:00Z")
        OffsetDateTime startDate,
        @Schema(type = "string", pattern = "^\\d{2}:\\d{2}(:\\d{2})?$", example = "12:21:23")
        LocalTime startTime,

        @Future
        @Schema(type = "string", format = "date-time", example = "2026-01-28T18:30:00Z")
        OffsetDateTime endDate,
        @Schema(type = "string", pattern = "^\\d{2}:\\d{2}(:\\d{2})?$", example = "18:30:00")
        LocalTime endTime,

        String status,
        UUID reservedById,

        TripType tripType,
        String meetupPoint,
        String tripIntention,
        String pricingMethod,

        Boolean isNegotiable,
        String paymentOption,
        String regularAmount,
        String discountPercentage,
        String discountedAmount
) {}
