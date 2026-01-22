package com.yowyob.template.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private UUID id;
    private UUID addressableId;
    private String addressableType;
    private String type;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String locality;
    private String zipCode;
    private String postalCode;
    private String poBox;
    private Boolean isDefault;
    private UUID countryId;
    private String neighborhood;
    private String informalDescription;
    private Double latitude;
    private Double longitude;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;
}
