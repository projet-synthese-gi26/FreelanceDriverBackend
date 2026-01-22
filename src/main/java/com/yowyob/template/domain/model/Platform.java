package com.yowyob.template.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Platform implements Reviewable, Reactable {
    private String version;

    public static final UUID PLATFORM_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Override
    public UUID getReviewableId() {
        return PLATFORM_ID;
    }

    @Override
    public SubjectType getReviewableType() {
        return SubjectType.PLATFORM;
    }

    @Override
    public Double getAverageRating() {
        return 0.0;
    }

    @Override
    public UUID getReactableId() {
        return PLATFORM_ID;
    }

    @Override
    public SubjectType getReactableType() {
        return SubjectType.PLATFORM;
    }

    @Override
    public Map<ReactionType, Long> getReactionCounts() {
        return new HashMap<>();
    }
}
