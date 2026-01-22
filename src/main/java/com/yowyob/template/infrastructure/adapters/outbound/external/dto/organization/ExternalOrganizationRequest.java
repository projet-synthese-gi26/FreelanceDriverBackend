package com.yowyob.template.infrastructure.adapters.outbound.external.dto.organization;

import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

public record ExternalOrganizationRequest(
    UUID businessActorId,
    String shortName,
    String longName,
    String service,
    String email,
    String description,
    String logoUri,
    String logoId,
    String websiteUrl,
    String socialNetwork,
    String businessRegistrationNumber,
    String taxNumber,
    BigDecimal capitalShare, // Using BigDecimal for numbers that represent money/large values
    String ceoName,
    Integer yearFounded,
    Integer numberOfEmployees,
    String legalForm,
    Boolean isIndividualBusiness,
    List<String> keywords
) {}
