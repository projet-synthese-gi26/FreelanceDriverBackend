package com.yowyob.template.domain.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleSimplified {
    private String makeName;
    private String modelName;
    private String transmissionType;
    private String manufacturerName;
    private String sizeName;
    private String typeName;
    private String fuelTypeName;
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
