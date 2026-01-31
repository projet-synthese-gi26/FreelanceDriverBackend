package com.yowyob.template.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerification {
    private UUID id;
    private String email;
    private String otp;
    private OffsetDateTime expiresAt;
    private Boolean verified;
    private OffsetDateTime createdAt;
}
