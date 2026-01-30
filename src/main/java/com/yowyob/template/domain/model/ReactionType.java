package com.yowyob.template.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "ReactionType",
        type = "string",
        enumAsRef = true
)
public enum ReactionType {
    LIKE, 
    DISLIKE, 
    LOVE, 
    ANGRY, 
    SAD, 
    LAUGH, 
    CELEBRATE
}
