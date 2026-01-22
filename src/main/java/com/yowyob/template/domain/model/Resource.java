package com.yowyob.template.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class Resource implements Reviewable, Reactable, IAsset {
    private UUID id;
    private UUID orgId;
    private String name;

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
        return SubjectType.VEHICLE; // Simplifying for resources that are currently mostly vehicles
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
        return SubjectType.VEHICLE;
    }

    @Override
    public Map<ReactionType, Long> getReactionCounts() {
        return new HashMap<>();
    }
}
