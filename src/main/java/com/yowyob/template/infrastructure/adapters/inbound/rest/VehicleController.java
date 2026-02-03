package com.yowyob.template.infrastructure.adapters.inbound.rest;

import com.yowyob.template.domain.model.Vehicle;
import com.yowyob.template.domain.model.VehicleSimplified;
import com.yowyob.template.domain.ports.out.VehicleRepositoryPort;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.VehicleResponse;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.VehicleIllustrationImageResponse;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.request.CreateVehicleRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.request.CreateVehicleSimplifiedRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.request.UpdateVehicleRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleRepositoryPort vehicleRepository;

    @GetMapping("/{id}/images")
    public Flux<VehicleIllustrationImageResponse> getVehicleImages(@PathVariable UUID id, ServerWebExchange exchange) {
        String authHeader = extractAuthToken(exchange);
        return vehicleRepository.getImages(id, authHeader)
                .map(img -> new VehicleIllustrationImageResponse(img.getVehicleIllustrationImageId(), img.getVehicleId(), img.getImagePath()));
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<VehicleIllustrationImageResponse> addVehicleImage(@PathVariable UUID id, @RequestPart("file") FilePart file, ServerWebExchange exchange) {
        String authHeader = extractAuthToken(exchange);
        return vehicleRepository.addImage(id, file, authHeader)
                .map(img -> new VehicleIllustrationImageResponse(img.getVehicleIllustrationImageId(), img.getVehicleId(), img.getImagePath()));
    }

    @DeleteMapping("/images/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteVehicleImage(@PathVariable UUID imageId, ServerWebExchange exchange) {
        String authHeader = extractAuthToken(exchange);
        return vehicleRepository.deleteImage(imageId, authHeader);
    }

    @GetMapping("/{id}")
    public Mono<VehicleResponse> getVehicle(@PathVariable UUID id, ServerWebExchange exchange) {
        String authHeader = extractAuthToken(exchange);
        return vehicleRepository.getVehicle(id, authHeader)
                .map(this::mapToResponse);
    }

    @GetMapping
    public Flux<VehicleResponse> getMyVehicles(ServerWebExchange exchange) {
        String authHeader = extractAuthToken(exchange);
        return vehicleRepository.getMyVehicles(authHeader)
                .map(this::mapToResponse);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Lister les véhicules d'un utilisateur",
            description = "Retourne les véhicules associés à l'utilisateur indiqué.")
    public Flux<VehicleResponse> getVehiclesByUserId(@PathVariable UUID userId) {
        return vehicleRepository.getVehiclesByUserId(userId)
                .map(this::mapToResponse);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<VehicleResponse> createVehicle(@RequestBody CreateVehicleRequest request, ServerWebExchange exchange) {
        String authHeader = extractAuthToken(exchange);
        Vehicle vehicle = mapFromRequest(request);
        return vehicleRepository.createVehicle(vehicle, authHeader)
                .map(this::mapToResponse);
    }

    @PostMapping("/simplified")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<VehicleResponse> createVehicleSimplified(@RequestBody CreateVehicleSimplifiedRequest request, ServerWebExchange exchange) {
        String authHeader = extractAuthToken(exchange);
        VehicleSimplified simplified = mapFromRequest(request);
        return vehicleRepository.createVehicleSimplified(simplified, authHeader)
                .map(this::mapToResponse);
    }

    @PutMapping("/{id}")
    public Mono<VehicleResponse> replaceVehicle(@PathVariable UUID id, @RequestBody UpdateVehicleRequest request, ServerWebExchange exchange) {
        String authHeader = extractAuthToken(exchange);
        Vehicle vehicle = mapFromRequest(request);
        return vehicleRepository.replaceVehicle(id, vehicle, authHeader)
                .map(this::mapToResponse);
    }

    @PatchMapping("/{id}")
    public Mono<VehicleResponse> updateVehicle(@PathVariable UUID id, @RequestBody UpdateVehicleRequest request, ServerWebExchange exchange) {
        String authHeader = extractAuthToken(exchange);
        Vehicle vehicle = mapFromRequest(request);
        return vehicleRepository.updateVehicle(id, vehicle, authHeader)
                .map(this::mapToResponse);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteVehicle(@PathVariable UUID id, ServerWebExchange exchange) {
        String authHeader = extractAuthToken(exchange);
        return vehicleRepository.deleteVehicle(id, authHeader);
    }

    private String extractAuthToken(ServerWebExchange exchange) {
        return exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    }

    // --- Mappers ---

    private VehicleResponse mapToResponse(Vehicle v) {
        return new VehicleResponse(
            v.getVehicleId(), v.getVehicleMakeId(), v.getVehicleModelId(), v.getTransmissionTypeId(),
            v.getManufacturerId(), v.getVehicleSizeId(), v.getVehicleTypeId(), v.getFuelTypeId(),
            v.getVehicleSerialNumber(), v.getVehicleSerialPhoto(), v.getRegistrationNumber(),
            v.getRegistrationPhoto(), v.getRegistrationExpiryDate(), v.getTankCapacity(),
            v.getLuggageMaxCapacity(), v.getTotalSeatNumber(), v.getAverageFuelConsumptionPerKm(),
            v.getMileageAtStart(), v.getMileageSinceCommissioning(), v.getVehicleAgeAtStart(),
            v.getBrand(), v.getCreatedAt(), v.getUpdatedAt()
        );
    }

    private Vehicle mapFromRequest(CreateVehicleRequest r) {
        return Vehicle.builder()
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
            .build();
    }

    private Vehicle mapFromRequest(UpdateVehicleRequest r) {
        return Vehicle.builder()
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
            .build();
    }

    private VehicleSimplified mapFromRequest(CreateVehicleSimplifiedRequest r) {
        return VehicleSimplified.builder()
            .makeName(r.makeName())
            .modelName(r.modelName())
            .transmissionType(r.transmissionType())
            .manufacturerName(r.manufacturerName())
            .sizeName(r.sizeName())
            .typeName(r.typeName())
            .fuelTypeName(r.fuelTypeName())
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
            .build();
    }
}
