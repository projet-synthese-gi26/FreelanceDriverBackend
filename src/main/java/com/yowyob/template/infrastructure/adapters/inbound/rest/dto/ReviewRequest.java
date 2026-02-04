package com.yowyob.template.infrastructure.adapters.inbound.rest.dto;

import com.yowyob.template.domain.model.ReviewType;
import com.yowyob.template.domain.model.SubjectType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    private UUID subjectId;
    @Schema(
            implementation = SubjectType.class,
            enumAsRef = true,
            type = "string",
            allowableValues = {"PRODUCT", "DRIVER", "CLIENT", "ORGANISATION", "VEHICLE", "PLATFORM", "REVIEW"}
    )
    private SubjectType subjectType;
    @Schema(implementation = ReviewType.class, enumAsRef = true, type = "string")
    private ReviewType reviewType;
    private Integer rating;
    private String comment;
    private String reportReason;
}
