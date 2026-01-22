package com.yowyob.template.application.service;

import com.yowyob.template.domain.model.Certification;
import com.yowyob.template.domain.ports.out.CertificationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DriverProfileService {

    private final CertificationRepositoryPort certificationRepository;

    public Mono<Certification> requestCertification(UUID orgId, String syndicateName) {
        // Logic: 
        // 1. Verify Org exists / is Driver Org?
        // 2. Create Certification object
        Certification cert = Certification.builder()
                .organizationId(orgId)
                .name(syndicateName)
                .type("SYNDICATE_LABEL") 
                .obtainementDate(Instant.now())
                .description("Provisional Certification Request")
                .build();
                
        return certificationRepository.save(cert);
    }
}
