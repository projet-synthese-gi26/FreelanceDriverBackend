package com.yowyob.template.infrastructure.adapters.outbound.persistence.entity;

import com.yowyob.template.domain.model.ReviewType;
import com.yowyob.template.domain.model.SubjectType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("review")
public class ReviewEntity {
    @Id
    private UUID id;
    private UUID authorId;
    private UUID subjectId;
    private SubjectType subjectType;
    private ReviewType reviewType;
    private Integer rating;
    private String comment;
    private String reportReason;
    private OffsetDateTime createdAt;
}
