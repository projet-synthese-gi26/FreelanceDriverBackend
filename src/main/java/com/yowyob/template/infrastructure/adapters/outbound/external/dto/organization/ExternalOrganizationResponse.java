package com.yowyob.template.infrastructure.adapters.outbound.external.dto.organization;

import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExternalOrganizationResponse(
    UUID id,
    String code,
    String service,
    UUID businessActorId,
    Boolean isIndividualBusiness,
    String email,
    String shortName,
    String longName,
    String description,
    String logoUri,
    String logoId,
    String websiteUrl,
    String socialNetwork,
    String businessRegistrationNumber,
    String taxNumber,
    BigDecimal capitalShare,
    String ceoName,
    Integer yearFounded,
    List<ExternalAddressResponse> addresses,
    List<ExternalContactResponse> contacts,
    List<String> keywords,
    Integer numberOfEmployees,
    String legalForm,
    Boolean isActive,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
