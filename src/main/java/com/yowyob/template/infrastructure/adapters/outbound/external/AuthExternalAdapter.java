package com.yowyob.template.infrastructure.adapters.outbound.external;

import com.yowyob.template.domain.exception.BadCredentialsException;
import com.yowyob.template.domain.ports.out.AuthClientPort;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.AuthLoginRequest;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.AuthResponse;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.RegisterRequest;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.TraMaSysUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthExternalAdapter implements AuthClientPort {
    private final WebClient.Builder webClientBuilder;

    @Value("${application.external.auth-service-url}")
    private String authServiceUrl;

    @Override
    public Mono<AuthResponse> login(String identifier, String password) {
        AuthLoginRequest request = new AuthLoginRequest(identifier, password);
        return webClientBuilder.baseUrl(authServiceUrl).build()
                .post()
                .uri("/api/auth/login")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    if (response.statusCode().equals(HttpStatus.UNAUTHORIZED)) {
                        return Mono.error(new BadCredentialsException("Invalid credentials"));
                    }
                    return response.bodyToMono(String.class)
                            .flatMap(body -> Mono.error(new RuntimeException("External API error: " + body)));
                })
                .bodyToMono(AuthResponse.class);
    }

    @Override
    public Mono<AuthResponse> registerUser(RegisterRequest request) {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("data", request, MediaType.APPLICATION_JSON);
        // If file was needed: bodyBuilder.part("file", resource);

        return webClientBuilder.baseUrl(authServiceUrl).build()
                .post()
                .uri("/api/auth/register")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> 
                    response.bodyToMono(String.class)
                            .flatMap(body -> Mono.error(new RuntimeException("Registration failed: " + body)))
                )
                .bodyToMono(AuthResponse.class);
    }
}
