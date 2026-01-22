package com.yowyob.template.infrastructure.adapters.outbound.persistence.entity;

import com.yowyob.template.domain.model.ReactionType;
import com.yowyob.template.domain.model.SubjectType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("reactions")
public class ReactionEntity {
    @Id
    private UUID id;
    private UUID actorId;
    private UUID targetId;
    private SubjectType targetType;
    private ReactionType type;
    private Timestamp createdAt;
}
