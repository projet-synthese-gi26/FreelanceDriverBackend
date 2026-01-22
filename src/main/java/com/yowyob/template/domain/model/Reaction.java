package com.yowyob.template.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reaction {
    private UUID id;
    private UUID actorId;
    private UUID targetId;
    private SubjectType targetType;
    private ReactionType type;
    private Timestamp createdAt;
}
