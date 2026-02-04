package com.yowyob.template.infrastructure.adapters.inbound.rest.dto.request;

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
    String brand
) {}
