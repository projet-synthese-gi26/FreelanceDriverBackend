package com.yowyob.template.infrastructure.adapters.inbound.rest.dto.request;

import com.yowyob.template.domain.model.TripType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.time.OffsetDateTime;

public record CreateAnnonceRequest(
        @NotBlank String title,
        @NotBlank String departureLocation,
        @NotBlank String dropoffLocation,
        @NotNull @Future
        @Schema(type = "string", format = "date-time", example = "2026-01-28T16:30:00Z")
        OffsetDateTime startDate,
        @NotNull
        @Schema(type = "string", pattern = "^\\d{2}:\\d{2}(:\\d{2})?$", example = "12:21:23")
        LocalTime startTime,
        @Schema(type = "string", format = "date-time", example = "2026-01-28T18:30:00Z")
        OffsetDateTime endDate,
        @Schema(type = "string", pattern = "^\\d{2}:\\d{2}(:\\d{2})?$", example = "18:30:00")
        LocalTime endTime,
        
        @NotBlank String cost,

        // Nouveaux champs
        TripType tripType,
        String meetupPoint,
        String tripIntention,
        String pricingMethod,

        // Champs optionnels
        String baggageInfo,
        Boolean isNegotiable,
        String paymentMethod
) {}
