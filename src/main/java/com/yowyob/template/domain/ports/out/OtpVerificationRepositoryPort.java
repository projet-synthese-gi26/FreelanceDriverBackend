package com.yowyob.template.domain.ports.out;

import com.yowyob.template.domain.model.OtpVerification;
import reactor.core.publisher.Mono;

public interface OtpVerificationRepositoryPort {
    Mono<OtpVerification> save(OtpVerification otpVerification);
    Mono<OtpVerification> findByEmail(String email);
    Mono<Void> deleteById(java.util.UUID id);
}
