package com.yowyob.template.infrastructure.mappers;

import com.yowyob.template.domain.model.*;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.ProductResponse;
import com.yowyob.template.infrastructure.adapters.outbound.persistence.entity.ProductEntity;
import java.time.ZoneOffset;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    default Product toDomain(ProductEntity entity) {
        if (entity == null) return null;

        if ("PLANNING".equalsIgnoreCase(entity.getProductType())) {
            Planning planning = Planning.builder()
                .paymentOption(entity.getPaymentOption())
                .regularAmount(entity.getRegularAmount())
                .discountPercentage(entity.getDiscountPercentage() != null ? entity.getDiscountPercentage().toString() : null)
                .discountedAmount(entity.getDiscountedAmount() != null ? entity.getDiscountedAmount().toString() : null)
                .build();
            mapCommonFieldsToDomain(planning, entity);
            return planning;

        } else if ("ANNONCE".equalsIgnoreCase(entity.getProductType())) {
            Annonce annonce = Annonce.builder()
                .cost(entity.getCost())
                .baggageInfo(entity.getBaggageInfo())
                .build();
            mapCommonFieldsToDomain(annonce, entity);
            return annonce;
        }
        return null;
    }

    default ProductEntity toEntity(Product domain) {
        if (domain == null) return null;
        ProductEntity entity = new ProductEntity();
        mapCommonFieldsToEntity(entity, domain);

        if (domain instanceof Planning planning) {
            entity.setProductType("PLANNING");
            entity.setPaymentOption(planning.getPaymentOption());
            entity.setRegularAmount(planning.getRegularAmount());
            if (planning.getDiscountPercentage() != null) entity.setDiscountPercentage(new java.math.BigDecimal(planning.getDiscountPercentage()));
            if (planning.getDiscountedAmount() != null) entity.setDiscountedAmount(new java.math.BigDecimal(planning.getDiscountedAmount()));
                
        } else if (domain instanceof Annonce annonce) {
            entity.setProductType("ANNONCE");
            entity.setCost(annonce.getCost());
            entity.setBaggageInfo(annonce.getBaggageInfo());
        }
        return entity;
    }

    // Mapper pour la réponse API
    ProductResponse toResponse(Product domain);
    
    private void mapCommonFieldsToEntity(ProductEntity entity, Product domain) {
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrgId());
        entity.setClientId(domain.getClientId());
        entity.setClientName(domain.getClientName());
        entity.setClientPhoneNumber(domain.getClientPhoneNumber());
        entity.setProfileImageUrl(domain.getProfileImageUrl());
        entity.setTitle(domain.getTitle());
        entity.setDepartureLocation(domain.getDepartureLocation());
        entity.setDropoffLocation(domain.getDropoffLocation());
        entity.setStartDate(domain.getStartDate());
        entity.setStartTime(domain.getStartTime());
        entity.setEndDate(domain.getEndDate());
        entity.setEndTime(domain.getEndTime());
        entity.setReservedById(domain.getReservedById());
        entity.setNegotiable(domain.isNegotiable());
        entity.setPaymentMethod(domain.getPaymentMethod());
        entity.setStatus(domain.getStatus());
        entity.setTripType(domain.getTripType());
        entity.setMeetupPoint(domain.getMeetupPoint());
        entity.setTripIntention(domain.getTripIntention());
        entity.setPricingMethod(domain.getPricingMethod());
        entity.setCreatedAt(domain.getCreatedAt() != null
                ? domain.getCreatedAt().toInstant().atOffset(ZoneOffset.UTC)
                : null);
        entity.setUpdatedAt(domain.getUpdatedAt() != null
                ? domain.getUpdatedAt().toInstant().atOffset(ZoneOffset.UTC)
                : null);
    }

    private void mapCommonFieldsToDomain(Product product, ProductEntity entity) {
        product.setId(entity.getId());
        product.setOrgId(entity.getOrganizationId());
        product.setClientId(entity.getClientId());
        product.setClientName(entity.getClientName());
        product.setClientPhoneNumber(entity.getClientPhoneNumber());
        product.setProfileImageUrl(entity.getProfileImageUrl());
        product.setTitle(entity.getTitle());
        product.setDepartureLocation(entity.getDepartureLocation());
        product.setDropoffLocation(entity.getDropoffLocation());
        product.setStartDate(entity.getStartDate());
        product.setStartTime(entity.getStartTime());
        product.setEndDate(entity.getEndDate());
        product.setEndTime(entity.getEndTime());
        product.setReservedById(entity.getReservedById());
        product.setNegotiable(entity.isNegotiable());
        product.setPaymentMethod(entity.getPaymentMethod());
        product.setStatus(entity.getStatus());
        product.setTripType(entity.getTripType());
        product.setMeetupPoint(entity.getMeetupPoint());
        product.setTripIntention(entity.getTripIntention());
        product.setPricingMethod(entity.getPricingMethod());
        product.setCreatedAt(entity.getCreatedAt() != null
                ? java.sql.Timestamp.from(entity.getCreatedAt().toInstant())
                : null);
        product.setUpdatedAt(entity.getUpdatedAt() != null
                ? java.sql.Timestamp.from(entity.getUpdatedAt().toInstant())
                : null);
    }
}
