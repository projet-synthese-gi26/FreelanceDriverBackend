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
public class Contact {
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
