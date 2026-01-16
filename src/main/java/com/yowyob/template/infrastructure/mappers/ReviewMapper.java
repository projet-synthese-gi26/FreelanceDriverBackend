
package com.yowyob.template.infrastructure.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

import com.yowyob.template.domain.model.Review;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.ReviewRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.ReviewResponse;
import com.yowyob.template.infrastructure.adapters.outbound.persistence.ReviewEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);

    // Request -> Domain
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Review toDomain(ReviewRequest request);

    // Domain -> Response
    ReviewResponse toResponse(Review review);

    // Domain <-> Entity

    @Mapping(target = "id", ignore = true) // For creation: force id to null for insert
    @Mapping(target = "createdAt", ignore = false)
    ReviewEntity toEntityForInsert(Review review);

    @Mapping(target = "id", ignore = false)
    @Mapping(target = "createdAt", ignore = false)
    ReviewEntity toEntity(Review review);

    @Mapping(target = "id", ignore = false)
    @Mapping(target = "createdAt", ignore = false)
    Review toDomain(ReviewEntity entity);

    // Optionally, ensure id is null after mapping for insert
    @AfterMapping
    default void setIdNullForInsert(Review review, @MappingTarget ReviewEntity entity) {
        entity.setId(null);
    }
}
