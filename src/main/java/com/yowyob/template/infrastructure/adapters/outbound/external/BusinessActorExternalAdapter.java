package com.yowyob.template.infrastructure.adapters.outbound.external;

import com.yowyob.template.domain.model.BusinessActor;
import com.yowyob.template.domain.model.ClientRole;
import com.yowyob.template.domain.model.DriverRole;
import com.yowyob.template.domain.ports.out.BusinessActorRepositoryPort;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.organization.ExternalBusinessActorRequest;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.organization.ExternalBusinessActorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class BusinessActorExternalAdapter implements BusinessActorRepositoryPort {

    private final WebClient.Builder webClientBuilder;

    @Value("${application.external.organization-service-url}")
    private String orgServiceUrl;

    private WebClient getClient() {
        return webClientBuilder.baseUrl(orgServiceUrl).build();
    }

    @Override
    public Mono<BusinessActor> save(BusinessActor businessActor) {
        return save(businessActor, null);
    }

    @Override
    public Mono<BusinessActor> save(BusinessActor businessActor, String jwtToken) {
        ExternalBusinessActorRequest request = mapToRequest(businessActor);
        var requestSpec = getClient().post()
                .uri("/api/v1/business-actors");
        
        if (jwtToken != null && !jwtToken.isEmpty()) {
            requestSpec.header("Authorization", "Bearer " + jwtToken);
        }

        return requestSpec
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ExternalBusinessActorResponse.class)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<BusinessActor> findById(UUID id) {
        return getClient().get()
                .uri("/api/v1/business-actors/{id}", id)
                .retrieve()
                .bodyToMono(ExternalBusinessActorResponse.class)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<BusinessActor> findByUserId(UUID userId) {
        return findByUserId(userId, null);
    }

    @Override
    public Mono<BusinessActor> findByUserId(UUID userId, String jwtToken) {
        var requestSpec = getClient().get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/business-actors")
                        .queryParam("authUserId", userId)
                        .build());
                        
        if (jwtToken != null && !jwtToken.isEmpty()) {
            requestSpec.header("Authorization", "Bearer " + jwtToken);
        }

        return requestSpec
                .retrieve()
                .bodyToFlux(ExternalBusinessActorResponse.class)
                .next() // Get first match
                .map(this::mapToDomain);
    }

    @Override
    public Flux<BusinessActor> findAll() {
        return getClient().get()
                .uri("/api/v1/business-actors")
                .retrieve()
                .bodyToFlux(ExternalBusinessActorResponse.class)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        // Not implemented in external requirements, but needed for interface compliance or could be a logical delete
        return Mono.error(new UnsupportedOperationException("Delete not supported externally yet"));
    }

    @Override
    public Mono<Void> deleteAll() {
        return Mono.error(new UnsupportedOperationException("Delete All not supported externally"));
    }

    private ExternalBusinessActorRequest mapToRequest(BusinessActor domain) {
        String type;
        if (domain instanceof DriverRole) {
            type = "DRIVER";
        } else {
            type = "FREELANCE";
        }

        // Split displayName into firstName/lastName if possible
        String[] specificNames = domain.getDisplayName() != null ? domain.getDisplayName().split(" ", 2) : new String[]{"", ""};
        String fName = specificNames[0];
        String lName = specificNames.length > 1 ? specificNames[1] : "";

        return new ExternalBusinessActorRequest(
            domain.getUserId(), // authUserId is UUID
            fName,
            lName,
            domain.getEmailAddress(),
            true, // isIndividual default
            true, // isAvailable default
            type,
            "User", // role placeholder
            Collections.emptyList(),
            Collections.emptyList()
        );
    }

    private BusinessActor mapToDomain(ExternalBusinessActorResponse response) {
        BusinessActor actor;
        // Use type to determine domain class
        if ("DRIVER".equalsIgnoreCase(response.type())) {
            actor = DriverRole.builder()
                    //@todo map driver specific fields if available
                    .build();
        } else {
            actor = ClientRole.builder().build();
        }
        
        actor.setId(response.id());
        actor.setUserId(response.authUserId()); // Already UUID
        actor.setDisplayName(response.firstName() + " " + response.lastName());
        actor.setEmailAddress(response.email());
        if (response.contacts() != null && !response.contacts().isEmpty()) {
            // Pick first phone? Or just leave null as phone is in Contact entity usually
             // actor.setPhoneNumber(...) 
        }
        
        return actor;
    }
}
