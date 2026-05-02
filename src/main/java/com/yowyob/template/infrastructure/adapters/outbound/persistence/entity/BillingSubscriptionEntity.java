package com.yowyob.template.infrastructure.adapters.outbound.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("billing_subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingSubscriptionEntity {
    @Id
    private UUID id;
    private UUID userId;
    private UUID planId;
    private String status;
    private Instant currentPeriodStart;
    private Instant currentPeriodEnd;
    private Boolean cancelAtPeriodEnd;
    private Instant createdAt;
    private Instant updatedAt;
}
