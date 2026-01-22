package com.yowyob.template.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;
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
public abstract class Organisation implements Reviewable, Reactable {
    private UUID id;
    private UUID actorId;
    private String name;
    private String description;
    private String taxId;
    private String logoUrl;
    
    // Additional fields from schema.sql
    private String code;
    private String service;
    private Boolean isIndividualBusiness;
    private String email;
    private String websiteUrl;
    private String socialNetwork;
    private Double businessRegistrationNumber;
    private String capitalShare;
    private String ceoName;
    private Timestamp yearFounded;
    private List<String> keywords;
    private Integer numberOfEmployees;
    private String legalForm;
    private Boolean isActive;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    @Builder.Default
    private List<Contact> contacts = new ArrayList<>();
    
    private Address address;

    public abstract Product createProduct(Map<String, Object> params);
    
    public void addResource(Resource res) {
        // Implementation for later
    }

    @Override
    public UUID getReviewableId() {
        return id;
    }

    @Override
    public SubjectType getReviewableType() {
        return SubjectType.ORGANISATION;
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
        return SubjectType.ORGANISATION;
    }

    @Override
    public Map<ReactionType, Long> getReactionCounts() {
        return new HashMap<>();
    }
}
