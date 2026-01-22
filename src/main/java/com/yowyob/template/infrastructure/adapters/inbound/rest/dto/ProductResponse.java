package com.yowyob.template.infrastructure.adapters.inbound.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private UUID id;
    private UUID organizationId;
    private String name;
    private String description;
    private Boolean isActive;
    private BigDecimal price;
    private String departureLocation;
    private String arrivalLocation;
    private OffsetDateTime startDate;
    private LocalTime startTime;
    private OffsetDateTime endDate;
    private LocalTime endTime;
    private String baggageInfo;
    private Boolean isNegotiable;
    private String paymentMethod;
    private String title;
    private String status;
    private String type;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private List<String> productUrls;
    private String cvUrl;
    private String category;
    private List<String> skills;
    private String regularAmount;
    private BigDecimal discountPercentage;
    private BigDecimal discountedAmount;
    private List<String> metadata;
}