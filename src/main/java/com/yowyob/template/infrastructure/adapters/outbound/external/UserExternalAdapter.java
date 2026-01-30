package com.yowyob.template.infrastructure.adapters.outbound.external;

import com.yowyob.template.domain.model.User;
import com.yowyob.template.domain.ports.out.UserRepositoryPort;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.ChangePasswordExternalRequest;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.TraMaSysUserResponse;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.UpdateUserExternalRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserExternalAdapter implements UserRepositoryPort {
    private final WebClient.Builder webClientBuilder;

    @Value("${application.external.auth-service-url}")
    private String authServiceUrl;

     private String toAuthorizationHeaderValue(String jwtToken) {
         if (jwtToken == null) {
             return null;
         }
         String token = jwtToken.trim();
         if (token.isEmpty()) {
             return null;
         }
         if (token.regionMatches(true, 0, "Bearer ", 0, "Bearer ".length())) {
             return token;
         }
         return "Bearer " + token;
     }

    @Override
    public Mono<User> findById(UUID id) {
        return findById(id, null);
    }

    @Override
    public Mono<User> findById(UUID id, String jwtToken) {
        var requestSpec = webClientBuilder.baseUrl(authServiceUrl).build()
                .get()
                .uri("/api/users/{id}", id);

        String authHeader = toAuthorizationHeaderValue(jwtToken);
        if (authHeader != null) {
            requestSpec.header("Authorization", authHeader);
        }

        return requestSpec
                .retrieve()
                .bodyToMono(TraMaSysUserResponse.class)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return webClientBuilder.baseUrl(authServiceUrl).build()
                .get()
                .uri("/api/users/email/{email}", email)
                .retrieve()
                .bodyToMono(TraMaSysUserResponse.class)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<User> updateUser(UUID id, User user, String jwtToken) {
        UpdateUserExternalRequest request = new UpdateUserExternalRequest(
                user.getFirstName(),
                user.getLastName(),
                user.getPhone()
        );
        var requestSpec = webClientBuilder.baseUrl(authServiceUrl).build()
                .put()
                .uri("/api/users/{id}", id)
                .bodyValue(request);
        String authHeader = toAuthorizationHeaderValue(jwtToken);
        if (authHeader != null) {
            requestSpec.header("Authorization", authHeader);
        }
        return requestSpec.retrieve()
                .bodyToMono(TraMaSysUserResponse.class)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<Void> updatePassword(UUID id, String currentPassword, String newPassword, String jwtToken) {
        ChangePasswordExternalRequest request = new ChangePasswordExternalRequest(currentPassword, newPassword);
        var requestSpec = webClientBuilder.baseUrl(authServiceUrl).build()
                .put()
                .uri("/api/users/{id}/password", id)
                .bodyValue(request);
        String authHeader = toAuthorizationHeaderValue(jwtToken);
        if (authHeader != null) {
            requestSpec.header("Authorization", authHeader);
        }
        return requestSpec.retrieve()
                .bodyToMono(Void.class);
    }

    @Override
    public Mono<User> updatePicture(UUID id, FilePart file, String jwtToken) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", file);
        var requestSpec = webClientBuilder.baseUrl(authServiceUrl).build()
                .post()
                .uri("/api/users/{id}/picture", id)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()));
        String authHeader = toAuthorizationHeaderValue(jwtToken);
        if (authHeader != null) {
            requestSpec.header("Authorization", authHeader);
        }
        return requestSpec.retrieve()
                .bodyToMono(TraMaSysUserResponse.class)
                .map(this::mapToDomain);
    }

    private User mapToDomain(TraMaSysUserResponse response) {
        if (response == null) return null;
        return User.builder()
                .id(response.id() != null ? UUID.fromString(response.id()) : null)
                .username(response.username())
                .email(response.email())
                .phone(response.phone())
                .firstName(response.firstName())
                .lastName(response.lastName())
                .photoUri(response.photoUri())
                .permissions(response.permissions() != null ? response.permissions() : new java.util.ArrayList<>())
                .build();
    }
}
