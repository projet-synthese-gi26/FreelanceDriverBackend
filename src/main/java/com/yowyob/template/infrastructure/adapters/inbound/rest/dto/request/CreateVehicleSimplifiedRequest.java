package com.yowyob.template.infrastructure.adapters.inbound.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CreateVehicleSimplifiedRequest(
    String makeName,
    String modelName,
    String transmissionType,
    String manufacturerName,
    String sizeName,
    String typeName,
    String fuelTypeName,
    String vehicleSerialNumber,
    String vehicleSerialPhoto,
    String registrationNumber,
    String registrationPhoto,
    LocalDateTime registrationExpiryDate,
    Double tankCapacity,
    Double luggageMaxCapacity,
    Integer totalSeatNumber,
    Double averageFuelConsumptionPerKm,
    Double mileageAtStart,
    Double mileageSinceCommissioning,
    Double vehicleAgeAtStart,
    String brand,
    @Schema(description = "Amenity: climatisation", example = "true")
    @NotNull Boolean airConditioned,
    @Schema(description = "Amenity: confort", example = "true")
    @NotNull Boolean comfortable,
    @Schema(description = "Amenity: sièges soft", example = "false")
    @NotNull Boolean soft,
    @Schema(description = "Amenity: écran", example = "false")
    @NotNull Boolean screen,
    @Schema(description = "Amenity: wifi", example = "true")
    @NotNull Boolean wifi,
    @Schema(description = "Amenity: péage", example = "false")
    @NotNull Boolean tollCharge,
    @Schema(description = "Amenity: parking", example = "true")
    @NotNull Boolean carParking,
    @Schema(description = "Amenity: alarme", example = "true")
    @NotNull Boolean alarm,
    @Schema(description = "Amenity: taxe routière", example = "false")
    @NotNull Boolean stateTax,
    @Schema(description = "Amenity: indemnité chauffeur", example = "false")
    @NotNull Boolean driverAllowance,
    @Schema(description = "Amenity: prise en charge / dépose", example = "true")
    @NotNull Boolean pickupAndDrop,
    @Schema(description = "Amenity: internet", example = "true")
    @NotNull Boolean internet,
    @Schema(description = "Amenity: animaux autorisés", example = "false")
    @NotNull Boolean petsAllow
) {}
