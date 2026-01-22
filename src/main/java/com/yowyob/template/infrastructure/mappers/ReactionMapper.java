package com.yowyob.template.infrastructure.mappers;

import com.yowyob.template.domain.model.Reaction;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.ReactionResponse;
import com.yowyob.template.infrastructure.adapters.outbound.persistence.entity.ReactionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReactionMapper {
    ReactionEntity toEntity(Reaction domain);
    Reaction toDomain(ReactionEntity entity);
    ReactionResponse toResponse(Reaction domain);
}
