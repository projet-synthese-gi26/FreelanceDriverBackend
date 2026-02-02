package com.yowyob.template.infrastructure.adapters.inbound.rest.dto.request;

import com.yowyob.template.domain.model.TripType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.time.OffsetDateTime;

public record CreateAnnonceRequest(
        @NotBlank String title,
        @NotBlank String departureLocation,
        @NotBlank String dropoffLocation,
        @NotNull @Future OffsetDateTime startDate,
        @NotNull LocalTime startTime,
        @NotNull @Future OffsetDateTime endDate,
        @NotNull LocalTime endTime,
        
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
