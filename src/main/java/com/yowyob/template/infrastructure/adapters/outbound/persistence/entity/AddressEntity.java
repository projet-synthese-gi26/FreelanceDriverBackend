package com.yowyob.template.infrastructure.adapters.outbound.persistence.entity;

import java.sql.Timestamp;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Table("Address")
@NoArgsConstructor
@AllArgsConstructor
public class AddressEntity {
    @Id
    private UUID id;
    String addressableType;
    String type;
    String addressLine1;
    String addressLine2;
    String city;
    String state;
    String locality;
    String zipCode;
    String postalCode;
    String poBox;
    Boolean isDefault;
    String neighborhood;
    String informalDescription;
    Double latitude;
    Double longitude;
    Timestamp createdAt;
    Timestamp updatedAt;
    Timestamp deletedAt;


    
}
