package com.yowyob.template.infrastructure.adapters.outbound.external;

import com.yowyob.template.domain.model.Vehicle;
import com.yowyob.template.domain.ports.out.VehicleRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VehicleExternalAdapter implements VehicleRepositoryPort {
    private final WebClient.Builder webClientBuilder;

    @Override
    public Mono<Vehicle> findById(UUID id) {
        return webClientBuilder.build()
                .get()
                .uri("http://vehicle-service/vehicles/{id}", id)
                .retrieve()
                .bodyToMono(Vehicle.class);
    }

    @Override
    public Flux<Vehicle> findByOrgId(UUID orgId) {
        return webClientBuilder.build()
                .get()
                .uri("http://vehicle-service/vehicles?orgId={orgId}", orgId)
                .retrieve()
                .bodyToFlux(Vehicle.class);
    }
}
