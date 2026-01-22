package com.yowyob.template.infrastructure.adapters.outbound.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("organization")
public class OrganizationEntity {
    @Id
    private UUID id;
    private UUID businessActorId;
    private String logoId;
    private String code;
    private String service;
    private Boolean isIndividualBusiness;
    private String email;
    private String shortName;
    private String longName;
    private String description;
    private String logoUri;
    private String websiteUrl;
    private String socialNetwork;
    private Double businessRegistrationNumber;
    private Double taxNumber;
    private String capitalShare;
    private String ceoName;
    private Timestamp yearFounded;
    private String[] keywords;
    private Integer numberOfEmployees;
    private String legalForm;
    private Boolean isActive;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;
    private String syndicateName;
}
