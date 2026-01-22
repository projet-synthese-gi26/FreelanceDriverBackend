package com.yowyob.template.infrastructure.adapters.inbound.rest.dto;

import java.util.List;
import java.util.UUID;

public record OrganisationRequest(
    UUID businessActorId,
    String shortName,
    String longName,
    String service,
    String email,
    String description,
    String logoUri,
    String websiteUrl,
    String socialNetwork,
    String businessRegistrationNumber,
    String taxNumber,
    String ceoName,
    Integer numberOfEmployees,
    String legalForm,
    Boolean isIndividualBusiness,
    List<String> keywords
) {}
