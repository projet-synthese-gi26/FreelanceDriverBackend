package com.yowyob.template.domain.ports.out;

import java.util.List;
import java.util.UUID;
import reactor.core.publisher.Mono;

// I'll define a Domain Model for Certification first or use a map/DTO?
// Ideally a Domain model.
// I'll create `Certification.java` in domain model.

public interface CertificationRepositoryPort {
    Mono<com.yowyob.template.domain.model.Certification> save(com.yowyob.template.domain.model.Certification certification);
    Mono<List<com.yowyob.template.domain.model.Certification>> findByOrganizationId(UUID organizationId);
}
