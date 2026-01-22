package com.yowyob.template.domain.model;

import java.util.UUID;

public interface Reviewable {
    UUID getReviewableId();
    SubjectType getReviewableType();
    Double getAverageRating();
}
