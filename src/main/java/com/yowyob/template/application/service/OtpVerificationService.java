package com.yowyob.template.application.service;

import com.yowyob.template.domain.model.OtpVerification;
import com.yowyob.template.domain.ports.out.OtpVerificationRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpVerificationService {
    private final OtpVerificationRepositoryPort otpRepository;

    public Mono<OtpVerification> verifyOtp(String email, String otp) {
        log.info("Verifying OTP for email={}", email);
        return otpRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
                .flatMap(record -> {
                    if (!record.getOtp().equals(otp)) {
                        return Mono.error(new IllegalArgumentException("Invalid OTP"));
                    }
                    if (record.getExpiresAt() != null && OffsetDateTime.now().isAfter(record.getExpiresAt())) {
                        return Mono.error(new IllegalArgumentException("OTP has expired"));
                    }
                    record.setVerified(true);
                    return otpRepository.deleteById(record.getId()).thenReturn(record);
                });
    }

    public Mono<OtpVerification> saveOtp(String email, String otp, OffsetDateTime expiresAt) {
        OtpVerification verification = OtpVerification.builder()
                .email(email)
                .otp(otp)
                .expiresAt(expiresAt)
                .verified(false)
                .createdAt(OffsetDateTime.now())
                .build();
        return otpRepository.save(verification);
    }
}
