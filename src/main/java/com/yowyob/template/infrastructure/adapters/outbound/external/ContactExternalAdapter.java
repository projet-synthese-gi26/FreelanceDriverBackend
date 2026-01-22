package com.yowyob.template.infrastructure.adapters.outbound.external;

import com.yowyob.template.domain.model.Contact;
import com.yowyob.template.domain.ports.out.ContactRepositoryPort;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.organization.ExternalContactRequest;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.organization.ExternalContactResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ContactExternalAdapter implements ContactRepositoryPort {

    private final WebClient.Builder webClientBuilder;

    @Value("${application.external.organization-service-url}")
    private String orgServiceUrl;

    private WebClient getClient() {
        return webClientBuilder.baseUrl(orgServiceUrl).build();
    }

    @Override
    public Mono<Contact> save(Contact contact) {
        ExternalContactRequest request = new ExternalContactRequest(
            contact.getContactableId(),
            contact.getContactableType(),
            contact.getTitle(),
            contact.getFirstName(),
            contact.getLastName(),
            contact.getEmail(),
            contact.getPhoneNumber(),
            contact.getSecondaryPhoneNumber(),
            contact.getFaxNumber(),
            null, // secondaryEmail missing in domain?
            contact.getIsFavorite()
        );

        return getClient().post()
                .uri("/api/v1/contacts")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ExternalContactResponse.class)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<Contact> findById(UUID id) {
        // Implementation if needed
        return Mono.empty();
    }

    @Override
    public Flux<Contact> findAll() {
        return Flux.empty();
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return getClient().delete()
                .uri("/api/v1/contacts/{id}", id)
                .retrieve()
                .bodyToMono(Void.class);
    }

    @Override
    public Mono<Void> deleteAll() {
        return Mono.error(new UnsupportedOperationException());
    }

    private Contact mapToDomain(ExternalContactResponse response) {
        return Contact.builder()
                .id(response.id())
                .contactableId(response.contactableId())
                .contactableType("ORGANIZATION") // Assumed
                .firstName(response.firstName())
                .lastName(response.lastName())
                .email(response.email())
                .phoneNumber(response.phoneNumber())
                .title(response.title())
                .isFavorite(response.isFavorite())
                .build();
    }
}
