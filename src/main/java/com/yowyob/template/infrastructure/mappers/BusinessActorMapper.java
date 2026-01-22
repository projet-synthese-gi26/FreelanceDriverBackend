package com.yowyob.template.infrastructure.mappers;

import com.yowyob.template.domain.model.BusinessActor;
import com.yowyob.template.domain.model.ClientRole;
import com.yowyob.template.domain.model.DriverRole;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.BusinessActorRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.BusinessActorResponse;
import com.yowyob.template.infrastructure.adapters.outbound.persistence.entity.BusinessActorEntity;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface BusinessActorMapper {
    BusinessActorEntity toEntity(BusinessActor domain);
    
    default BusinessActor toDomain(BusinessActorEntity entity) {
        if (entity == null) return null;
        return ClientRole.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .displayName(entity.getDisplayName())
                .phoneNumber(entity.getPhoneNumber())
                .emailAddress(entity.getEmailAddress())
                .avatarUrl(entity.getAvatarUrl())
                .build();
    }

    BusinessActorResponse toResponse(BusinessActor domain);

    default BusinessActor toDomain(BusinessActorRequest request) {
        if (request == null) return null;
        UUID userId = request.userId() != null ? UUID.fromString(request.userId()) : null;
        if ("DRIVER".equalsIgnoreCase(request.role())) {
            return DriverRole.builder()
                    .displayName(request.name())
                    .phoneNumber(request.phoneNumber())
                    .emailAddress(request.emailAddress())
                    .userId(userId)
                    .build();
        }
        return ClientRole.builder()
                .displayName(request.name())
                .phoneNumber(request.phoneNumber())
                .emailAddress(request.emailAddress())
                .userId(userId)
                .build();
    }
}
