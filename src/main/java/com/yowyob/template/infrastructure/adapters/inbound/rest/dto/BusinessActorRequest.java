package com.yowyob.template.infrastructure.adapters.inbound.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record BusinessActorRequest(

        @NotBlank String userId,
        @NotBlank String name,
        @NotBlank String phoneNumber,
        @Email String emailAddress) {
}