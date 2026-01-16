package com.yowyob.template.infrastructure.mappers;

import com.yowyob.template.domain.model.Address;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.AddressRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.AddressResponse;
import com.yowyob.template.infrastructure.adapters.outbound.persistence.entity.AddressEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    AddressEntity toEntity(Address domain);
    Address toDomain(AddressEntity entity);

    Address toDomain (AddressRequest request);

    AddressResponse toResponse(Address domain);
}