package com.yowyob.template.infrastructure.adapters.outbound.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.LocalTime;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Table("products")
@Data 
@NoArgsConstructor 
@AllArgsConstructor
public class ProductEntity {
    @Id
    private UUID id;
    private UUID organizationId;
    private String name;
    private String description;
    private Boolean isActive;
    private String standardPrice;
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
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private List<String> productUrls;
    private String regularAmount;
    private BigDecimal discountPercentage;
    private BigDecimal discountedAmount;
    private List<String> metadata;
    private String productType; // ANNONCE, PLANNING, CV

    // Annonce specific
    private OffsetDateTime requiredDate;
    private String category;

    // CV specific
    private List<String> skills;
    private String fileUrl;

    // Planning specific
    private Integer availableSeats;
}