package com.yowyob.template.infrastructure.adapters.inbound.rest.dto;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

public record OrganisationResponse(
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
    String websiteUrl,
    String socialNetwork,
    String businessRegistrationNumber,
    String taxNumber,
    String ceoName,
    Integer numberOfEmployees,
    String legalForm,
    Boolean isActive,
    String status,
    List<AddressResponse> addresses,
    List<ContactResponse> contacts,
    List<String> keywords,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
