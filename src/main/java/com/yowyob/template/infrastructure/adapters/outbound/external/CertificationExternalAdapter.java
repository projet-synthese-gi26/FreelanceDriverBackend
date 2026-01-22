package com.yowyob.template.infrastructure.adapters.outbound.external;

import com.yowyob.template.domain.model.Certification;
import com.yowyob.template.domain.ports.out.CertificationRepositoryPort;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.organization.ExternalCertificationRequest;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.organization.ExternalCertificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CertificationExternalAdapter implements CertificationRepositoryPort {

    private final WebClient.Builder webClientBuilder;

    @Value("${application.external.organization-service-url}")
    private String orgServiceUrl;

    private WebClient getClient() {
        return webClientBuilder.baseUrl(orgServiceUrl).build();
    }

    @Override
    public Mono<Certification> save(Certification certification) {
        ExternalCertificationRequest request = new ExternalCertificationRequest(
            certification.getOrganizationId(),
            certification.getName(),
            certification.getType(),
            certification.getDescription(),
            certification.getObtainementDate()
        );

        return getClient().post()
                .uri("/api/v1/certifications")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ExternalCertificationResponse.class)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<List<Certification>> findByOrganizationId(UUID organizationId) {
        return getClient().get()
                .uri("/api/v1/certifications/organization/{organizationId}", organizationId)
                .retrieve()
                .bodyToFlux(ExternalCertificationResponse.class)
                .map(this::mapToDomain)
                .collectList();
    }

    private Certification mapToDomain(ExternalCertificationResponse response) {
        return Certification.builder()
                .id(response.id())
                .organizationId(response.organizationId())
                .name(response.name())
                .type(response.type())
                .description(response.description())
                .obtainementDate(response.obtainementDate() != null ? response.obtainementDate().toInstant(java.time.ZoneOffset.UTC) : null)
                .createdAt(response.createdAt() != null ? response.createdAt().toInstant(java.time.ZoneOffset.UTC) : null)
                .build();
    }
}
