// CHEMIN: src/main/java/com/yowyob/template/infrastructure/adapters/outbound/persistence/entity/ProductEntity.java

package com.yowyob.template.infrastructure.adapters.outbound.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.yowyob.template.domain.model.ProductStatus;
import com.yowyob.template.domain.model.TripType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.LocalTime;
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

    // Champs dénormalisés
    private UUID clientId;
    private String clientName;
    private String clientPhoneNumber;
    private String profileImageUrl;

    // Champs communs
    private String title;
    private ProductStatus status;
    private TripType tripType;
    private boolean isNegotiable;
    private String paymentMethod;
    private String departureLocation;
    private String dropoffLocation;
    private String meetupPoint;
    private String tripIntention;
    private String pricingMethod;
    private OffsetDateTime startDate;
    private LocalTime startTime;
    private OffsetDateTime endDate;
    private LocalTime endTime;
    private UUID reservedById;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // Champs Annonce
    private String cost;
    private String baggageInfo;

    // Champs Planning
    private String paymentOption;
    private String regularAmount;
    private BigDecimal discountPercentage;
    private BigDecimal discountedAmount;

    // Champ technique
    private String productType;
}