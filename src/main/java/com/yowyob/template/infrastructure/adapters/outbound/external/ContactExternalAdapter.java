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
        return save(contact, null);
    }

    @Override
    public Mono<Contact> save(Contact contact, String jwtToken) {
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
            contact.getSecondaryEmail(),
            contact.getIsFavorite(),
            contact.getIsEmailVerified(),
            contact.getIsPhoneNumberVerified()
        );

        var requestSpec = getClient().post()
                .uri("/api/v1/contacts");

        if (jwtToken != null && !jwtToken.isEmpty()) {
            requestSpec.header("Authorization", "Bearer " + jwtToken);
        }

        return requestSpec
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ExternalContactResponse.class)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<Contact> update(Contact contact, String jwtToken) {
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
            contact.getSecondaryEmail(),
            contact.getIsFavorite(),
            contact.getIsEmailVerified(),
            contact.getIsPhoneNumberVerified()
        );

        var requestSpec = getClient().put()
                .uri("/api/v1/contacts/{id}", contact.getId());

        if (jwtToken != null && !jwtToken.isEmpty()) {
            requestSpec.header("Authorization", "Bearer " + jwtToken);
        }

        return requestSpec
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
    public Flux<Contact> findAllByContactableId(UUID contactableId) {
        return findAllByContactableId(contactableId, null);
    }

    @Override
    public Flux<Contact> findAllByContactableId(UUID contactableId, String jwtToken) {
        var requestSpec = getClient().get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/contacts")
                        .queryParam("contactableId", contactableId)
                        .build());

        if (jwtToken != null && !jwtToken.isEmpty()) {
            requestSpec.header("Authorization", "Bearer " + jwtToken);
        }

        return requestSpec
                .retrieve()
                .bodyToFlux(ExternalContactResponse.class)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return deleteById(id, null);
    }

    @Override
    public Mono<Void> deleteById(UUID id, String jwtToken) {
        var requestSpec = getClient().delete()
                .uri("/api/v1/contacts/{id}", id);

        if (jwtToken != null && !jwtToken.isEmpty()) {
            requestSpec.header("Authorization", "Bearer " + jwtToken);
        }

        return requestSpec
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
                .contactableType(response.contactableType())
                .firstName(response.firstName())
                .lastName(response.lastName())
                .email(response.email())
                .phoneNumber(response.phoneNumber())
                .secondaryPhoneNumber(response.secondaryPhoneNumber())
                .faxNumber(response.faxNumber())
                .secondaryEmail(response.secondaryEmail())
                .title(response.title())
                .isFavorite(response.isFavorite())
                .isEmailVerified(response.isEmailVerified())
                .isPhoneNumberVerified(response.isPhoneNumberVerified())
                .createdAt(response.createdAt() != null ? java.sql.Timestamp.valueOf(response.createdAt()) : null)
                .build();
    }
}
