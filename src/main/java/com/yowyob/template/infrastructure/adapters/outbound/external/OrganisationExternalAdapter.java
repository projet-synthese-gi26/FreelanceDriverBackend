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

     private String toAuthorizationHeaderValue(String jwtToken) {
         if (jwtToken == null) {
             return null;
         }
         String token = jwtToken.trim();
         if (token.isEmpty()) {
             return null;
         }
         if (token.regionMatches(true, 0, "Bearer ", 0, "Bearer ".length())) {
             return token;
         }
         return "Bearer " + token;
     }

    @Override
    public Mono<Organisation> save(Organisation organisation) {
        return save(organisation, null);
    }

    @Override
    public Mono<Organisation> save(Organisation organisation, String jwtToken) {
        ExternalOrganizationRequest request = mapToRequest(organisation);
        var requestSpec = getClient().post()
                .uri("/api/v1/organizations");

        String authHeader = toAuthorizationHeaderValue(jwtToken);
        if (authHeader != null) {
            requestSpec.header("Authorization", authHeader);
        }

        return requestSpec
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ExternalOrganizationResponse.class)
                .map(this::mapToDomainBasic);
    }

    @Override
    public Mono<Organisation> findById(UUID id) {
        return findById(id, null);
    }

    @Override
    public Mono<Organisation> findById(UUID id, String jwtToken) {
        var requestSpec = getClient().get()
                .uri("/api/v1/organizations/{id}", id);

        String authHeader = toAuthorizationHeaderValue(jwtToken);
        if (authHeader != null) {
            requestSpec.header("Authorization", authHeader);
        }

        Mono<ExternalOrganizationResponse> orgMono = requestSpec
                .retrieve()
                .bodyToMono(ExternalOrganizationResponse.class);
        return enrichOrganisation(orgMono);
    }

    @Override
    public Mono<Organisation> findByActorId(UUID actorId) {
         return findByActorId(actorId, null);
    }

    @Override
    public Mono<Organisation> findByActorId(UUID actorId, String jwtToken) {
        var requestSpec = getClient().get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/organizations")
                        .queryParam("actorId", actorId)
                        .build());
                        
        String authHeader = toAuthorizationHeaderValue(jwtToken);
        if (authHeader != null) {
            requestSpec.header("Authorization", authHeader);
        }

        return requestSpec
                .retrieve()
                .bodyToFlux(ExternalOrganizationResponse.class)
                .next()
                .flatMap(orgResponse -> enrichOrganisation(Mono.just(orgResponse)));
    }

    private Mono<Organisation> enrichOrganisation(Mono<ExternalOrganizationResponse> orgMono) {
         return orgMono.flatMap(orgResponse -> {
            Mono<List<ExternalCertificationResponse>> certsMono = getClient().get()
                .uri("/api/v1/certifications/organization/{id}", orgResponse.id())
                .retrieve()
                .bodyToFlux(ExternalCertificationResponse.class)
                .collectList()
                .onErrorResume(e -> Mono.just(Collections.emptyList()));

            return certsMono.map(certs -> {
                Organisation baseOrg = mapToDomainBasic(orgResponse);
                if (!certs.isEmpty()) {
                    CertifiedOrganisation certifiedOrg = new CertifiedOrganisation();
                    certifiedOrg.setWrappedOrganisation(baseOrg);
                    certifiedOrg.setSyndicateName(certs.get(0).name());
                    certifiedOrg.setId(baseOrg.getId());
                    certifiedOrg.setName(baseOrg.getName());
                    certifiedOrg.setActorId(baseOrg.getActorId());
                    return certifiedOrg;
                }
                return baseOrg;
            });
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
       
        if (response.contacts() != null) {
            org.setContacts(response.contacts().stream()
                .map(this::mapContact)
                .collect(Collectors.toList()));
        }

        return org;
    }

    private Contact mapContact(ExternalContactResponse dto) {
        return Contact.builder()
                .id(dto.id())
                .contactableId(dto.contactableId())
                .contactableType(dto.contactableType())
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .title(dto.title())
                .email(dto.email())
                .phoneNumber(dto.phoneNumber())
                .secondaryPhoneNumber(dto.secondaryPhoneNumber())
                .isFavorite(dto.isFavorite())
                .build();
    }
}
