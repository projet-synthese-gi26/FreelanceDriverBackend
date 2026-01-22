package com.yowyob.template.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Planning extends Product {
    private OffsetDateTime departureTime;
    private OffsetDateTime arrivalTime;
    private Address departureLocation;
    private Address arrivalLocation;
    private Integer availableSeats;
    private Boolean baggageAllowed;
    private Boolean isNegotiable;
    
    // Fields from schema.sql that match
    private LocalTime startTime;
    private LocalTime endTime;
    private String baggageInfo;
    private String paymentMethod;
}
