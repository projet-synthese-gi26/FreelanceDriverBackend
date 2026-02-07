package com.yowyob.template.infrastructure.mappers;

import com.yowyob.template.domain.model.UserDevice;
import com.yowyob.template.infrastructure.adapters.outbound.persistence.entity.UserDeviceEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDeviceMapper {
    UserDeviceEntity toEntity(UserDevice domain);
    UserDevice toDomain(UserDeviceEntity entity);
}
