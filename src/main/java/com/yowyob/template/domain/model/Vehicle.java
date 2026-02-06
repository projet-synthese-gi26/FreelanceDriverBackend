package com.yowyob.template.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.With;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@With
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle {
    private UUID vehicleId;
    private UUID vehicleMakeId;
    private UUID vehicleModelId;
    private UUID transmissionTypeId;
    private UUID manufacturerId;
    private UUID vehicleSizeId;
    private UUID vehicleTypeId;
    private UUID fuelTypeId;
    private String vehicleSerialNumber;
    private String vehicleSerialPhoto;
    private String registrationNumber;
    private String registrationPhoto;
    private LocalDateTime registrationExpiryDate;
    private Double tankCapacity;
    private Double luggageMaxCapacity;
    private Integer totalSeatNumber;
    private Double averageFuelConsumptionPerKm;
    private Double mileageAtStart;
    private Double mileageSinceCommissioning;
    private Double vehicleAgeAtStart;
    private String brand;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean airConditioned;
    private Boolean comfortable;
    private Boolean soft;
    private Boolean screen;
    private Boolean wifi;
    private Boolean tollCharge;
    private Boolean carParking;
    private Boolean alarm;
    private Boolean stateTax;
    private Boolean driverAllowance;
    private Boolean pickupAndDrop;
    private Boolean internet;
    private Boolean petsAllow;
}
