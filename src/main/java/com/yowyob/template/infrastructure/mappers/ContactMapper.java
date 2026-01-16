package com.yowyob.template.infrastructure.mappers;

import com.yowyob.template.domain.model.Contact;
import com.yowyob.template.infrastructure.adapters.outbound.persistence.entity.ContactEntity;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.ContactRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.ContactResponse;
import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ContactMapper {
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "emailVerifiedAt", ignore = true),
            @Mapping(target = "phoneVerifiedAt", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "deletedAt", ignore = true)
    })
    ContactEntity toEntity(Contact domain);

    // Ne pas ignorer id, createdAt, updatedAt pour le mapping depuis l'entité
    Contact toDomain(ContactEntity entity);

    ContactResponse toResponse(Contact domain);

    Contact toDomain(ContactRequest response);
}