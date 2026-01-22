package com.yowyob.template.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Certification {
    private UUID id;
    private UUID organizationId;
    private String name;
    private String type;
    private String description;
    private Instant obtainementDate;
    private Instant createdAt;
}
