package com.yowyob.template.infrastructure.adapters.inbound.rest.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateVehicleRequest(
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
    String brand
) {}
