package com.yowyob.template.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class Product implements Reviewable, Reactable, IAsset {
    
    private UUID id;
    private UUID orgId;

    // Données dénormalisées de l'auteur
    private UUID clientId;
    private String clientName;
    private String clientPhoneNumber;
    private String profileImageUrl;

    // Champs communs
    private String title;
    private String departureLocation;
    private String dropoffLocation;
    private OffsetDateTime startDate;
    private LocalTime startTime;
    private OffsetDateTime endDate;
    private LocalTime endTime;
    private UUID reservedById;
    private boolean isNegotiable;
    private String paymentMethod;
    private ProductStatus status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Nouveaux champs du formulaire
    private TripType tripType;
    private String meetupPoint;
    private String tripIntention;
    private String pricingMethod;
    
    @Builder.Default
    private List<String> metadata = new ArrayList<>();

    // Méthodes des interfaces
    @Override
    public UUID getAssetId() { return id; }

    @Override
    public UUID getOwnerId() { return orgId; }

    @Override
    public UUID getReviewableId() { return id; }

    @Override
    public SubjectType getReviewableType() { return SubjectType.PRODUCT; }

    @Override
    public Double getAverageRating() { return 0.0; } // Logique à implémenter

    @Override
    public UUID getReactableId() { return id; }

    @Override
    public SubjectType getReactableType() { return SubjectType.PRODUCT; }

    @Override
    public Map<ReactionType, Long> getReactionCounts() { return new HashMap<>(); } // Logique à implémenter
}