package com.yowyob.template.infrastructure.adapters.outbound.external;

import com.yowyob.template.domain.model.Address;
import com.yowyob.template.domain.ports.out.AddressRepositoryPort;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.organization.ExternalAddressRequest;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.organization.ExternalAddressResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AddressExternalAdapter implements AddressRepositoryPort {

    private final WebClient.Builder webClientBuilder;

    @Value("${application.external.organization-service-url}")
    private String orgServiceUrl;

    private WebClient getClient() {
        return webClientBuilder.baseUrl(orgServiceUrl).build();
    }

    @Override
    public Mono<Address> save(Address address) {
        ExternalAddressRequest request = new ExternalAddressRequest(
            address.getAddressableId(),
            address.getAddressableType(),
            address.getType(),
            address.getAddressLine1(),
            address.getAddressLine2(),
            address.getCity(),
            address.getState(),
            address.getLocality(),
            address.getZipCode(),
            null, // countryId
            address.getPoBox(),
            address.getNeighborhood(),
            address.getInformalDescription(),
            address.getIsDefault(),
            0.0, // lat
            0.0 // lon
        );
        
        // Correction on ID mapping:
        // The Request needs `addressableId` which is the Org ID.
        // The Domain `Address` object might define it, or I need to pass it.
        // I will suspect `Address` has it or I need to find a way to set it.
        // I'll check Address.java again in a moment.
        
        return getClient().post()
                .uri("/api/v1/addresses")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ExternalAddressResponse.class)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<Address> findById(UUID id) {
        return Mono.empty();
    }

    @Override
    public Flux<Address> findAll() {
        return Flux.empty();
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return getClient().delete()
                .uri("/api/v1/addresses/{id}", id)
                .retrieve()
                .bodyToMono(Void.class);
    }

    @Override
    public Mono<Void> deleteAll() {
        return Mono.error(new UnsupportedOperationException());
    }

    private Address mapToDomain(ExternalAddressResponse response) {
        return Address.builder()
                .id(response.id())
                .addressLine1(response.addressLine1())
                .city(response.city())
                .build();
    }
}
