package com.yowyob.template.infrastructure.adapters.outbound.persistence.entity;

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
@Table("review")
public class ReviewEntity {
    @Id
    private UUID id;
    private UUID rideId;
    private UUID authorId;
    private UUID subjectId;
    private SubjectType subjectType;
    private Integer rating;
    private String comment;
    private Timestamp createdAt;
}
