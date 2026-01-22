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
    private String title;
    private String description;
    private ProductStatus status;
    private BigDecimal price;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Additional fields from schema.sql
    private String name;
    private Boolean isActive;
    private String standardPrice;
    private List<String> productUrls;
    private String regularAmount;
    private BigDecimal discountPercentage;
    private BigDecimal discountedAmount;
    
    @Builder.Default
    private List<String> metadata = new ArrayList<>();

    public void publish() {
        this.status = ProductStatus.PUBLISHED;
    }

    @Override
    public UUID getAssetId() {
        return id;
    }

    @Override
    public UUID getOwnerId() {
        return orgId;
    }

    @Override
    public UUID getReviewableId() {
        return id;
    }

    @Override
    public SubjectType getReviewableType() {
        return SubjectType.PRODUCT;
    }

    @Override
    public Double getAverageRating() {
        return 0.0;
    }

    @Override
    public UUID getReactableId() {
        return id;
    }

    @Override
    public SubjectType getReactableType() {
        return SubjectType.PRODUCT;
    }

    @Override
    public Map<ReactionType, Long> getReactionCounts() {
        return new HashMap<>();
    }
}
