package com.yowyob.template.infrastructure.adapters.outbound.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.sql.Timestamp;
import java.util.UUID;

@Table("contact")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactEntity {
    @Id
    private UUID id;
    private UUID contactableId;
    private String contactableType;
    private String firstName;
    private String lastName;
    private String title;
    private Boolean isEmailVerified;
    private Boolean isPhoneNumberVerified;
    private Boolean isFavorite;
    private String phoneNumber;
    private String secondaryPhoneNumber;
    private String faxNumber;
    private String email;
    private String secondaryEmail;
    private Timestamp emailVerifiedAt;
    private Timestamp phoneVerifiedAt;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;
}
