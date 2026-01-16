package com.yowyob.template.infrastructure.mappers;

import com.yowyob.template.domain.model.BusinessActor;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.BusinessActorRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.BusinessActorResponse;
import com.yowyob.template.infrastructure.adapters.outbound.persistence.entity.BusinessActorEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BusinessActorMapper {
    BusinessActorEntity toEntity(BusinessActor domain);
    BusinessActor toDomain(BusinessActorEntity entity);

    BusinessActorResponse toResponse(BusinessActor domain);

    BusinessActor toDomain(BusinessActorRequest request);

}