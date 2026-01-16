package com.yowyob.template.infrastructure.adapters.inbound.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ContactRequest(

        @NotBlank String contactableType,
        String firstName,
        String lastName,
        String title,
        Boolean isEmailVerified,
        Boolean isPhoneNumberVerified,
        Boolean isFavorite,
        String phoneNumber,
        String secondaryPhoneNumber,
        String faxNumber,
        @Email String email,
        String secondaryEmail) {
}