package com.yowyob.template.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BusinessActor implements Reviewable, Reactable {
    private UUID id;
    private UUID userId;
    private String displayName;
    private String phoneNumber;
    private String emailAddress;
    private String avatarUrl;
    private List<String> languages;

    public abstract String getRoleType();

    @Override
    public UUID getReviewableId() {
        return id;
    }

    @Override
    public UUID getReactableId() {
        return id;
    }

    @Override
    public Double getAverageRating() {
        return 0.0;
    }

    @Override
    public Map<ReactionType, Long> getReactionCounts() {
        return new HashMap<>();
    }
}
