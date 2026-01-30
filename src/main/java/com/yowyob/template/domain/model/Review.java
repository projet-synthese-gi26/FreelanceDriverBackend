package com.yowyob.template.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review implements Reactable {
    private UUID id;
    private UUID authorId;
    private UUID subjectId;
    private SubjectType subjectType;
    private Integer rating;
    private String comment;
    private Boolean isVerifiedPurchase;
    private OffsetDateTime createdAt;

    @Override
    public UUID getReactableId() {
        return id;
    }

    @Override
    public SubjectType getReactableType() {
        return SubjectType.REVIEW;
    }

    @Override
    public Map<ReactionType, Long> getReactionCounts() {
        return new HashMap<>();
    }
}
