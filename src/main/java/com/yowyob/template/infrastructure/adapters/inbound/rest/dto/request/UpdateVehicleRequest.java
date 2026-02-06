package com.yowyob.template.infrastructure.adapters.inbound.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateVehicleRequest(
    UUID vehicleMakeId,
    UUID vehicleModelId,
    UUID transmissionTypeId,
    UUID manufacturerId,
    UUID vehicleSizeId,
    UUID vehicleTypeId,
    UUID fuelTypeId,
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
    Boolean airConditioned,
    @Schema(description = "Amenity: confort", example = "true")
    Boolean comfortable,
    @Schema(description = "Amenity: sièges soft", example = "false")
    Boolean soft,
    @Schema(description = "Amenity: écran", example = "false")
    Boolean screen,
    @Schema(description = "Amenity: wifi", example = "true")
    Boolean wifi,
    @Schema(description = "Amenity: péage", example = "false")
    Boolean tollCharge,
    @Schema(description = "Amenity: parking", example = "true")
    Boolean carParking,
    @Schema(description = "Amenity: alarme", example = "true")
    Boolean alarm,
    @Schema(description = "Amenity: taxe routière", example = "false")
    Boolean stateTax,
    @Schema(description = "Amenity: indemnité chauffeur", example = "false")
    Boolean driverAllowance,
    @Schema(description = "Amenity: prise en charge / dépose", example = "true")
    Boolean pickupAndDrop,
    @Schema(description = "Amenity: internet", example = "true")
    Boolean internet,
    @Schema(description = "Amenity: animaux autorisés", example = "false")
    Boolean petsAllow
) {}
