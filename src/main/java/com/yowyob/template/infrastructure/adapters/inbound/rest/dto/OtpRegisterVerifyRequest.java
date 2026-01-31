package com.yowyob.template.infrastructure.adapters.inbound.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record OtpRegisterVerifyRequest(
        @Email @NotBlank String email,
        @NotBlank String otp,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String phone,
        @NotBlank String role,
        String organisationName,
        String organisationDescription,
        String title,
        String address,
        @NotBlank String password
) {
}
