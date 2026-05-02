package com.yowyob.template.infrastructure.adapters.outbound.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Table("billing_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingPlanEntity {
    @Id
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
