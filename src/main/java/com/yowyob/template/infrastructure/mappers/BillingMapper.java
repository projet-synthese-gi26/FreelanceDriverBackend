package com.yowyob.template.infrastructure.mappers;

import com.yowyob.template.domain.model.BillingPlan;
import com.yowyob.template.domain.model.BillingSubscription;
import com.yowyob.template.domain.model.SubscriptionStatus;
import com.yowyob.template.infrastructure.adapters.outbound.persistence.entity.BillingPlanEntity;
import com.yowyob.template.infrastructure.adapters.outbound.persistence.entity.BillingSubscriptionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = SubscriptionStatus.class)
public interface BillingMapper {

    BillingPlan toDomain(BillingPlanEntity entity);

    BillingPlanEntity toEntity(BillingPlan domain);

    @Mapping(target = "status", expression = "java(entity.getStatus() == null ? null : SubscriptionStatus.valueOf(entity.getStatus()))")
    BillingSubscription toDomain(BillingSubscriptionEntity entity);

    @Mapping(target = "status", expression = "java(domain.getStatus() == null ? null : domain.getStatus().name())")
    BillingSubscriptionEntity toEntity(BillingSubscription domain);
}
