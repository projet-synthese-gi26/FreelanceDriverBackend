package com.yowyob.template.infrastructure.mappers;

import com.yowyob.template.domain.model.OtpVerification;
import com.yowyob.template.infrastructure.adapters.outbound.persistence.entity.OtpVerificationEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OtpVerificationMapper {
    OtpVerificationEntity toEntity(OtpVerification domain);
    OtpVerification toDomain(OtpVerificationEntity entity);
}
