package com.yowyob.template.infrastructure.adapters.outbound.persistence;

import com.yowyob.template.domain.model.OtpVerification;
import com.yowyob.template.domain.ports.out.OtpVerificationRepositoryPort;
import com.yowyob.template.infrastructure.adapters.outbound.persistence.repository.OtpVerificationR2dbcRepository;
import com.yowyob.template.infrastructure.mappers.OtpVerificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OtpVerificationR2dbcAdapter implements OtpVerificationRepositoryPort {
    private final OtpVerificationR2dbcRepository repository;
    private final OtpVerificationMapper mapper;

    @Override
    public Mono<OtpVerification> save(OtpVerification otpVerification) {
        return repository.save(mapper.toEntity(otpVerification))
                .map(mapper::toDomain);
    }

    @Override
    public Mono<OtpVerification> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id);
    }
}
