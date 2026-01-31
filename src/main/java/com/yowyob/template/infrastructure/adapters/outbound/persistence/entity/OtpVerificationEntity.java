package com.yowyob.template.infrastructure.adapters.outbound.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("otp_verifications")
public class OtpVerificationEntity {
    @Id
    private UUID id;
    private String email;
    private String otp;
    private OffsetDateTime expiresAt;
    private Boolean verified;
    private OffsetDateTime createdAt;
}
