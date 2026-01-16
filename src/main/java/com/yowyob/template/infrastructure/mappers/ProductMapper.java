package com.yowyob.template.infrastructure.mappers;

import com.yowyob.template.domain.model.Product;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.ProductRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.ProductResponse;
import com.yowyob.template.infrastructure.adapters.outbound.persistence.entity.ProductEntity;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mappings({
            @Mapping(target = "status", source = "status")
    })
    Product toDomain(ProductRequest request);

    Product toDomain(Mono<ProductRequest> requestMono);

    @Mappings({
            @Mapping(target = "status", source = "status")
    })
    ProductResponse toResponse(Product domain);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "status", source = "status")
    })
    ProductEntity toEntity(Product domain); // Pour la création, id ignoré

    // Pour le mapping retour (lecture), on ne doit pas ignorer l'id
    @Mappings({
            @Mapping(target = "id", ignore = false),
            @Mapping(target = "createdAt", ignore = false),
            @Mapping(target = "updatedAt", ignore = false),
            @Mapping(target = "status", source = "status")
    })
    Product toDomain(ProductEntity entity);
}