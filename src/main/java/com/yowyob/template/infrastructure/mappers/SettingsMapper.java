package com.yowyob.template.infrastructure.mappers;

import com.yowyob.template.domain.model.Settings;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.SettingsRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.SettingsResponse;
import com.yowyob.template.infrastructure.adapters.outbound.persistence.entity.SettingsEntity;
import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface SettingsMapper {
    @Mappings({
        @Mapping(target = "createdAt", ignore = true),
        @Mapping(target = "updatedAt", ignore = true)
    })
    SettingsEntity toEntity(Settings domain);

    @Mappings({
        @Mapping(target = "createdAt", ignore = true),
        @Mapping(target = "updatedAt", ignore = true)
    })
    Settings toDomain(SettingsEntity entity);

    Settings toDomain(SettingsRequest request);
    SettingsResponse toResponse(Settings domain);
}