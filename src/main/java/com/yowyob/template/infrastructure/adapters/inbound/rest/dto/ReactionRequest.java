package com.yowyob.template.infrastructure.adapters.inbound.rest.dto;

import com.yowyob.template.domain.model.ReactionType;
import com.yowyob.template.domain.model.SubjectType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
public class ReactionRequest {
    private UUID targetId;
    @Schema(
            implementation = SubjectType.class,
            enumAsRef = true,
            type = "string",
            allowableValues = {"PRODUCT", "DRIVER", "CLIENT", "ORGANISATION", "VEHICLE", "PLATFORM", "REVIEW"}
    )
    private SubjectType targetType;
    @Schema(
            implementation = ReactionType.class,
            enumAsRef = true,
            type = "string",
            allowableValues = {"LIKE", "DISLIKE", "LOVE", "ANGRY", "SAD", "LAUGH", "CELEBRATE"}
    )
    private ReactionType type;
}
