package com.yowyob.template.domain.ports.in;

import com.yowyob.template.domain.model.Organisation;
import com.yowyob.template.domain.model.OrganisationBuilder;
import com.yowyob.template.domain.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

public interface OrganisationUseCase {
    Mono<Organisation> createOrganisation(String name, UUID actorId, OrganisationBuilder.OrgType type);
    Mono<Organisation> getOrganisationById(UUID id);
    Flux<Organisation> getAllOrganisations();
    Mono<Organisation> certifyOrganisation(UUID id, String syndicateName);
    Mono<Void> deleteOrganisation(UUID id);
    Mono<Product> createProductForOrganisation(UUID id, Map<String, Object> params);
}
