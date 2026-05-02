package com.yowyob.template.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingPlan {
    private UUID id;
    private String code;
    private String name;
    private BigDecimal price;
    private String currency;
    private String period;
    private Boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
