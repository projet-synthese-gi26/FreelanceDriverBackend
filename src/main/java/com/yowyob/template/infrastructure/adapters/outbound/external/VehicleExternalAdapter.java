package com.yowyob.template.infrastructure.adapters.outbound.external;

import com.yowyob.template.domain.model.Vehicle;
import com.yowyob.template.domain.model.VehicleIllustrationImage;
import com.yowyob.template.domain.model.VehicleSimplified;
import com.yowyob.template.domain.ports.out.VehicleRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VehicleExternalAdapter implements VehicleRepositoryPort {
    private final WebClient.Builder webClientBuilder;
    private static final Logger log = LoggerFactory.getLogger(VehicleExternalAdapter.class);

    @Value("${application.external.auth-service-url}")
    private String serviceUrl;

    @Override
    public Flux<VehicleIllustrationImage> getImages(UUID vehicleId, String jwtToken) {
        return webClientBuilder.baseUrl(serviceUrl).build()
                .get()
                .uri("/vehicles/{id}/images", vehicleId)
                .headers(h -> {
                    if (jwtToken != null) h.setBearerAuth(extractToken(jwtToken));
                })
                .retrieve()
                .bodyToFlux(ExternalVehicleImageResponse.class)
                .map(this::mapImageToDomain);
    }

    @Override
    public Mono<VehicleIllustrationImage> addImage(UUID vehicleId, FilePart file, String jwtToken) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", file);

        return webClientBuilder.baseUrl(serviceUrl).build()
                .post()
                .uri("/vehicles/{id}/images", vehicleId)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .headers(h -> {
                    if (jwtToken != null) h.setBearerAuth(extractToken(jwtToken));
                })
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(ExternalVehicleImageResponse.class)
                .map(this::mapImageToDomain);
    }

    @Override
    public Mono<Void> deleteImage(UUID imageId, String jwtToken) {
        return webClientBuilder.baseUrl(serviceUrl).build()
                .delete()
                .uri("/vehicles/images/{imageId}", imageId)
                .headers(h -> {
                    if (jwtToken != null) h.setBearerAuth(extractToken(jwtToken));
                })
                .retrieve()
                .bodyToMono(Void.class);
    }

    @Override
    public Mono<Vehicle> getVehicle(UUID id, String jwtToken) {
        return webClientBuilder.baseUrl(serviceUrl).build()
                .get()
                .uri("/vehicles/{id}", id)
                .headers(h -> {
                    if (jwtToken != null) h.setBearerAuth(extractToken(jwtToken));
                })
                .retrieve()
                .bodyToMono(ExternalVehicleResponse.class)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<Vehicle> updateVehicle(UUID id, Vehicle vehicle, String jwtToken) {
        return webClientBuilder.baseUrl(serviceUrl).build()
                .patch()
                .uri("/vehicles/{id}", id)
                .headers(h -> {
                    if (jwtToken != null) h.setBearerAuth(extractToken(jwtToken));
                })
                .bodyValue(mapToRequest(vehicle))
                .retrieve()
                .bodyToMono(ExternalVehicleResponse.class)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<Vehicle> replaceVehicle(UUID id, Vehicle vehicle, String jwtToken) {
        return webClientBuilder.baseUrl(serviceUrl).build()
                .put()
                .uri("/vehicles/{id}", id)
                .headers(h -> {
                    if (jwtToken != null) h.setBearerAuth(extractToken(jwtToken));
                })
                .bodyValue(mapToRequest(vehicle))
                .retrieve()
                .bodyToMono(ExternalVehicleResponse.class)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<Void> deleteVehicle(UUID id, String jwtToken) {
        return webClientBuilder.baseUrl(serviceUrl).build()
                .delete()
                .uri("/vehicles/{id}", id)
                .headers(h -> {
                    if (jwtToken != null) h.setBearerAuth(extractToken(jwtToken));
                })
                .retrieve()
                .bodyToMono(Void.class);
    }

    @Override
    public Flux<Vehicle> getMyVehicles(String jwtToken) {
        return webClientBuilder.baseUrl(serviceUrl).build()
                .get()
                .uri("/vehicles")
                .headers(h -> {
                    if (jwtToken != null) h.setBearerAuth(extractToken(jwtToken));
                })
                .retrieve()
                .bodyToFlux(ExternalVehicleResponse.class)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<Vehicle> createVehicle(Vehicle vehicle, String jwtToken) {
        return webClientBuilder.baseUrl(serviceUrl).build()
                .post()
                .uri("/vehicles")
                .headers(h -> {
                    if (jwtToken != null) h.setBearerAuth(extractToken(jwtToken));
                })
                .bodyValue(mapToRequest(vehicle))
                .retrieve()
                .bodyToMono(ExternalVehicleResponse.class)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<Vehicle> createVehicleSimplified(VehicleSimplified vs, String jwtToken) {
        log.info("Creating simplified vehicle with make: {}, model: {}", vs.getMakeName(), vs.getModelName());
        return webClientBuilder.baseUrl(serviceUrl).build()
                .post()
                .uri("/vehicles/simplified")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h -> {
                    if (jwtToken != null) h.setBearerAuth(extractToken(jwtToken));
                })
                .bodyValue(vs) 
                .retrieve()
                .onStatus(httpStatus -> !httpStatus.is2xxSuccessful(), response -> {
                    log.error("Error from external vehicle service: {} - {}", response.statusCode(), response.headers().asHttpHeaders());
                    return response.bodyToMono(String.class)
                        .doOnNext(body -> log.error("Error body: {}", body))
                        .flatMap(body -> Mono.error(new RuntimeException("Failed to create simplified vehicle: " + response.statusCode() + " - " + body)));
                })
                .bodyToMono(ExternalVehicleResponse.class)
                .map(this::mapToDomain);
    }
    
    // --- Helpers & Inner Classes ---

    private String extractToken(String jwtToken) {
        if (jwtToken.startsWith("Bearer ")) {
            return jwtToken.substring(7);
        }
        return jwtToken;
    }

    private ExternalVehicleRequest mapToRequest(Vehicle v) {
        return new ExternalVehicleRequest(
            v.getVehicleMakeId(), v.getVehicleModelId(), v.getTransmissionTypeId(),
            v.getManufacturerId(), v.getVehicleSizeId(), v.getVehicleTypeId(),
            v.getFuelTypeId(), v.getVehicleSerialNumber(), v.getVehicleSerialPhoto(),
            v.getRegistrationNumber(), v.getRegistrationPhoto(), v.getRegistrationExpiryDate(),
            v.getTankCapacity(), v.getLuggageMaxCapacity(), v.getTotalSeatNumber(),
            v.getAverageFuelConsumptionPerKm(), v.getMileageAtStart(), 
            v.getMileageSinceCommissioning(), v.getVehicleAgeAtStart(), v.getBrand()
        );
    }

    private Vehicle mapToDomain(ExternalVehicleResponse r) {
        return Vehicle.builder()
            .vehicleId(r.vehicleId())
            .vehicleMakeId(r.vehicleMakeId())
            .vehicleModelId(r.vehicleModelId())
            .transmissionTypeId(r.transmissionTypeId())
            .manufacturerId(r.manufacturerId())
            .vehicleSizeId(r.vehicleSizeId())
            .vehicleTypeId(r.vehicleTypeId())
            .fuelTypeId(r.fuelTypeId())
            .vehicleSerialNumber(r.vehicleSerialNumber())
            .vehicleSerialPhoto(r.vehicleSerialPhoto())
            .registrationNumber(r.registrationNumber())
            .registrationPhoto(r.registrationPhoto())
            .registrationExpiryDate(r.registrationExpiryDate())
            .tankCapacity(r.tankCapacity())
            .luggageMaxCapacity(r.luggageMaxCapacity())
            .totalSeatNumber(r.totalSeatNumber())
            .averageFuelConsumptionPerKm(r.averageFuelConsumptionPerKm())
            .mileageAtStart(r.mileageAtStart())
            .mileageSinceCommissioning(r.mileageSinceCommissioning())
            .vehicleAgeAtStart(r.vehicleAgeAtStart())
            .brand(r.brand())
            .createdAt(r.createdAt())
            .updatedAt(r.updatedAt())
            .build();
    }
    
    private VehicleIllustrationImage mapImageToDomain(ExternalVehicleImageResponse r) {
        return VehicleIllustrationImage.builder()
            .vehicleIllustrationImageId(r.vehicleIllustrationImageId())
            .vehicleId(r.vehicleId())
            .imagePath(r.imagePath())
            .build();
    }

    // External DTOs
    record ExternalVehicleRequest(
        UUID vehicleMakeId, UUID vehicleModelId, UUID transmissionTypeId,
        UUID manufacturerId, UUID vehicleSizeId, UUID vehicleTypeId,
        UUID fuelTypeId, String vehicleSerialNumber, String vehicleSerialPhoto,
        String registrationNumber, String registrationPhoto, LocalDateTime registrationExpiryDate,
        Double tankCapacity, Double luggageMaxCapacity, Integer totalSeatNumber,
        Double averageFuelConsumptionPerKm, Double mileageAtStart, 
        Double mileageSinceCommissioning, Double vehicleAgeAtStart, String brand
    ) {}

    record ExternalVehicleResponse(
        UUID vehicleId, UUID vehicleMakeId, UUID vehicleModelId, UUID transmissionTypeId,
        UUID manufacturerId, UUID vehicleSizeId, UUID vehicleTypeId, UUID fuelTypeId,
        String vehicleSerialNumber, String vehicleSerialPhoto, String registrationNumber,
        String registrationPhoto, LocalDateTime registrationExpiryDate, Double tankCapacity,
        Double luggageMaxCapacity, Integer totalSeatNumber, Double averageFuelConsumptionPerKm,
        Double mileageAtStart, Double mileageSinceCommissioning, Double vehicleAgeAtStart,
        String brand, LocalDateTime createdAt, LocalDateTime updatedAt
    ) {}
    
    record ExternalVehicleImageResponse(
        UUID vehicleIllustrationImageId,
        UUID vehicleId,
        String imagePath
    ) {}
}
