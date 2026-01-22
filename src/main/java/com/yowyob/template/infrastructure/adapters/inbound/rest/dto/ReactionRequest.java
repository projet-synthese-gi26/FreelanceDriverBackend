package com.yowyob.template.infrastructure.adapters.inbound.rest.dto;

import com.yowyob.template.domain.model.ReactionType;
import com.yowyob.template.domain.model.SubjectType;
import lombok.Data;

import java.util.UUID;

@Data
public class ReactionRequest {
    private UUID actorId;
    private UUID targetId;
    private SubjectType targetType;
    private ReactionType type;
}
