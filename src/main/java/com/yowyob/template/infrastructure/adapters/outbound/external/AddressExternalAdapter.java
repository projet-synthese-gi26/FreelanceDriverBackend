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

    @Value("${application.external.organization-service-url:https://organization-service.pynfi.com}")
    private String orgServiceUrl;

    private WebClient getClient() {
        return webClientBuilder.baseUrl(orgServiceUrl).build();
    }

    @Override
    public Mono<Address> save(Address address) {
        return save(address, null);
    }

    @Override
    public Mono<Address> save(Address address, String jwtToken) {
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

        var requestSpec = getClient().post()
                .uri("/api/v1/addresses");

        if (jwtToken != null && !jwtToken.isEmpty()) {
            requestSpec.header("Authorization", "Bearer " + jwtToken);
        }

        return requestSpec
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ExternalAddressResponse.class)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<Address> update(Address address, String jwtToken) {
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
            address.getLatitude() != null ? address.getLatitude() : 0.0,
            address.getLongitude() != null ? address.getLongitude() : 0.0
        );

        var requestSpec = getClient().put()
                .uri("/api/v1/addresses/{id}", address.getId());

        if (jwtToken != null && !jwtToken.isEmpty()) {
            requestSpec.header("Authorization", "Bearer " + jwtToken);
        }

        return requestSpec
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
    public Flux<Address> findAllByAddressableId(UUID addressableId, String jwtToken) {
        var requestSpec = getClient().get()
                .uri("/api/v1/addresses/parent/ORGANIZATION/{id}", addressableId);
                        
        if (jwtToken != null && !jwtToken.isEmpty()) {
            requestSpec.header("Authorization", "Bearer " + jwtToken);
        }

        return requestSpec
                .retrieve()
                .bodyToFlux(ExternalAddressResponse.class)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return deleteById(id, null);
    }

    @Override
    public Mono<Void> deleteById(UUID id, String jwtToken) {
        var requestSpec = getClient().delete()
                .uri("/api/v1/addresses/{id}", id);

        if (jwtToken != null && !jwtToken.isEmpty()) {
            requestSpec.header("Authorization", "Bearer " + jwtToken);
        }
        
        return requestSpec
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
                .addressableId(response.addressableId())
                .addressableType(response.addressableType())
                .type(response.type())
                .addressLine1(response.addressLine1())
                .addressLine2(response.addressLine2())
                .city(response.city())
                .state(response.state())
                .locality(response.locality())
                .zipCode(response.zipCode())
                .countryId(response.countryId())
                .poBox(response.poBox())
                .neighborhood(response.neighborHood())
                .informalDescription(null)
                .isDefault(response.isDefault())
                .latitude(response.latitude())
                .longitude(response.longitude())
                .createdAt(response.createdAt() != null ? java.sql.Timestamp.valueOf(response.createdAt()) : null)
                .build();
    }
}
