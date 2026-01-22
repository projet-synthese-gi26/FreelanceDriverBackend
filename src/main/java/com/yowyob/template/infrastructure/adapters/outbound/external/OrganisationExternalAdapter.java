package com.yowyob.template.infrastructure.adapters.outbound.external;

import com.yowyob.template.domain.model.*;
import com.yowyob.template.domain.ports.out.OrganisationRepositoryPort;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.organization.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrganisationExternalAdapter implements OrganisationRepositoryPort {

    private final WebClient.Builder webClientBuilder;

    @Value("${application.external.organization-service-url}")
    private String orgServiceUrl;

    private WebClient getClient() {
        return webClientBuilder.baseUrl(orgServiceUrl).build();
    }

    @Override
    public Mono<Organisation> save(Organisation organisation) {
        ExternalOrganizationRequest request = mapToRequest(organisation);
        return getClient().post()
                .uri("/api/v1/organizations")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ExternalOrganizationResponse.class)
                .map(this::mapToDomainBasic);
    }

    @Override
    public Mono<Organisation> findById(UUID id) {
        Mono<ExternalOrganizationResponse> orgMono = getClient().get()
                .uri("/api/v1/organizations/{id}", id)
                .retrieve()
                .bodyToMono(ExternalOrganizationResponse.class);

        Mono<List<ExternalCertificationResponse>> certsMono = getClient().get()
                .uri("/api/v1/certifications/organization/{id}", id)
                .retrieve()
                .bodyToFlux(ExternalCertificationResponse.class)
                .collectList()
                .onErrorResume(e -> Mono.just(Collections.emptyList())); // Fallback if 404 or error

        return Mono.zip(orgMono, certsMono)
                .map(tuple -> {
                    ExternalOrganizationResponse orgResponse = tuple.getT1();
                    List<ExternalCertificationResponse> certs = tuple.getT2();
                    
                    Organisation baseOrg = mapToDomainBasic(orgResponse);
                    
                    if (!certs.isEmpty()) {
                        // Decorate
                        CertifiedOrganisation certifiedOrg = new CertifiedOrganisation();
                        certifiedOrg.setWrappedOrganisation(baseOrg);
                        certifiedOrg.setSyndicateName(certs.get(0).name()); // Use first cert name as syndicate/label
                        // Copy basic fields to the wrapper if needed, or rely on delegation
                        certifiedOrg.setId(baseOrg.getId());
                        certifiedOrg.setName(baseOrg.getName());
                        certifiedOrg.setActorId(baseOrg.getActorId());
                        return certifiedOrg;
                    }
                    
                    return baseOrg;
                });
    }

    @Override
    public Flux<Organisation> findAll() {
        return getClient().get()
                .uri("/api/v1/organizations")
                .retrieve()
                .bodyToFlux(ExternalOrganizationResponse.class)
                .map(this::mapToDomainBasic);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return getClient().delete()
                .uri("/api/v1/organizations/{id}", id)
                .retrieve()
                .bodyToMono(Void.class);
    }

    private ExternalOrganizationRequest mapToRequest(Organisation domain) {
        return new ExternalOrganizationRequest(
                domain.getActorId(),
                domain.getName(), // shortName
                domain.getName(), // longName - duplicate for now
                domain.getService(),
                domain.getEmail(),
                domain.getDescription(),
                domain.getLogoUrl(),
                domain.getActorId().toString(), // logoId placeholder - using actorId as proxy per example?
                domain.getWebsiteUrl(),
                domain.getSocialNetwork(), 
                "0000", // businessRegistrationNumber placeholder
                domain.getTaxId(),
                null, // capital
                domain.getCeoName(),
                null, // yearFounded
                domain.getNumberOfEmployees(),
                domain.getLegalForm(),
                domain.getIsIndividualBusiness(),
                domain.getKeywords()
        );
    }

    private Organisation mapToDomainBasic(ExternalOrganizationResponse response) {
        Organisation org;
        if ("TRANSPORT".equalsIgnoreCase(response.service()) || "LETS_GO".equalsIgnoreCase(response.service())) {
             org = DriverOrganisation.builder().build();
        } else {
             org = ClientOrganisation.builder().build();
        }
        
        org.setId(response.id());
        org.setActorId(response.businessActorId());
        org.setName(response.shortName()); 
        org.setDescription(response.description());
        org.setEmail(response.email());
        org.setService(response.service());
        org.setLogoUrl(response.logoUri());
        org.setTaxId(response.taxNumber());
        org.setWebsiteUrl(response.websiteUrl());
        org.setSocialNetwork(response.socialNetwork());
        
        // Map other fields
        
        return org;
    }
}
