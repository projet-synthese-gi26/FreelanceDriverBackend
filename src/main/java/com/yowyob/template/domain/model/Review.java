package com.yowyob.template.domain.model;

import java.sql.Timestamp;
import java.util.UUID;

public record Review(
        UUID id,
        UUID rideId,
        UUID authorId,
        UUID subjectId,
        Integer rating,
        String comment,
        Timestamp createdAt) {
}
