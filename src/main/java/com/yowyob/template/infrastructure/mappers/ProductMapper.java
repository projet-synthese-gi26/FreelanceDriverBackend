package com.yowyob.template.infrastructure.mappers;

import com.yowyob.template.domain.model.*;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.ProductRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.ProductResponse;
import com.yowyob.template.infrastructure.adapters.outbound.persistence.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "orgId", source = "organizationId")
    @Mapping(target = "id", ignore = false)
    default Product toDomain(ProductEntity entity) {
        if (entity == null) return null;
        
        Product.ProductBuilder<?, ?> builder;
        String type = entity.getProductType();
        
        if ("PLANNING".equalsIgnoreCase(type)) {
            builder = Planning.builder()
                    .availableSeats(entity.getAvailableSeats())
                    .startTime(entity.getStartTime())
                    .endTime(entity.getEndTime())
                    .departureTime(entity.getStartDate())
                    .arrivalTime(entity.getEndDate())
                    .baggageAllowed(entity.getBaggageInfo() != null)
                    .baggageInfo(entity.getBaggageInfo())
                    .paymentMethod(entity.getPaymentMethod())
                    .isNegotiable(entity.getIsNegotiable());
        } else if ("CV".equalsIgnoreCase(type)) {
            builder = CV.builder()
                    .skills(entity.getSkills())
                    .fileUrl(entity.getFileUrl());
        } else if ("ANNONCE".equalsIgnoreCase(type)) {
            builder = Annonce.builder()
                    .requiredDate(entity.getRequiredDate())
                    .category(entity.getCategory());
        } else {
            builder = Annonce.builder();
        }

        return builder
                .id(entity.getId())
                .orgId(entity.getOrganizationId())
                .title(entity.getTitle())
                .name(entity.getName())
                .description(entity.getDescription())
                .status(entity.getStatus() != null ? ProductStatus.valueOf(entity.getStatus()) : null)
                .isActive(entity.getIsActive())
                .standardPrice(entity.getStandardPrice())
                .productUrls(entity.getProductUrls())
                .regularAmount(entity.getRegularAmount())
                .discountPercentage(entity.getDiscountPercentage())
                .discountedAmount(entity.getDiscountedAmount())
                .metadata(entity.getMetadata())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Mapping(target = "organizationId", source = "orgId")
    default ProductEntity toEntity(Product domain) {
        if (domain == null) return null;
        
        ProductEntity entity = new ProductEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrgId());
        entity.setTitle(domain.getTitle());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setStatus(domain.getStatus() != null ? domain.getStatus().name() : null);
        entity.setIsActive(domain.getIsActive());
        entity.setStandardPrice(domain.getStandardPrice());
        entity.setProductUrls(domain.getProductUrls());
        entity.setRegularAmount(domain.getRegularAmount());
        entity.setDiscountPercentage(domain.getDiscountPercentage());
        entity.setDiscountedAmount(domain.getDiscountedAmount());
        entity.setMetadata(domain.getMetadata());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        if (domain instanceof Planning planning) {
            entity.setProductType("PLANNING");
            entity.setAvailableSeats(planning.getAvailableSeats());
            entity.setStartTime(planning.getStartTime());
            entity.setEndTime(planning.getEndTime());
            entity.setStartDate(planning.getDepartureTime());
            entity.setEndDate(planning.getArrivalTime());
            entity.setBaggageInfo(planning.getBaggageInfo());
            entity.setPaymentMethod(planning.getPaymentMethod());
            entity.setIsNegotiable(planning.getIsNegotiable());
        } else if (domain instanceof CV cv) {
            entity.setProductType("CV");
            entity.setSkills(cv.getSkills());
            entity.setFileUrl(cv.getFileUrl());
        } else if (domain instanceof Annonce annonce) {
            entity.setProductType("ANNONCE");
            entity.setRequiredDate(annonce.getRequiredDate());
            entity.setCategory(annonce.getCategory());
        }

        return entity;
    }

    default ProductResponse toResponse(Product domain) {
        if (domain == null) return null;
        ProductResponse response = new ProductResponse();
        response.setId(domain.getId());
        response.setTitle(domain.getTitle());
        response.setDescription(domain.getDescription());
        response.setStatus(domain.getStatus() != null ? domain.getStatus().name() : null);
        response.setPrice(domain.getPrice());
        
        if (domain instanceof Planning) response.setType("PLANNING");
        else if (domain instanceof CV) response.setType("CV");
        else if (domain instanceof Annonce) response.setType("ANNONCE");
        
        return response;
    }

    default Product toDomain(ProductRequest request) {
        if (request == null) return null;
        return null; 
    }
}
