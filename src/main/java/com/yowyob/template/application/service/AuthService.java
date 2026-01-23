package com.yowyob.template.application.service;

import com.yowyob.template.domain.model.User;
import com.yowyob.template.domain.ports.in.AuthUseCase;
import com.yowyob.template.domain.ports.out.AuthClientPort;
import com.yowyob.template.domain.ports.out.BusinessActorRepositoryPort;
import com.yowyob.template.domain.ports.out.OrganisationRepositoryPort;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.UserProfileResponse;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {
    private final AuthClientPort authClientPort;
    private final BusinessActorRepositoryPort businessActorRepository;
    private final OrganisationRepositoryPort organisationRepository;

    @Override
    public Mono<UserProfileResponse> login(String identifier, String password) {
        return authClientPort.login(identifier, password)
                .flatMap(authResponse -> {
                    var externalUser = authResponse.user();
                    UUID userId = UUID.fromString(externalUser.id());
                    
                    User user = User.builder()
                            .id(userId)
                            .email(externalUser.email())
                            .username(externalUser.username())
                            // ... other fields from externalUser map to User
                            .firstName(externalUser.firstName())
                            .lastName(externalUser.lastName())
                            .photoUri(externalUser.photoUri())
                            .build();

                    // Now fetch actor and organization
                    // We need findByUserId on Actor Repo and findByActorId on Org Repo
                    String token = authResponse.accessToken();

                    return businessActorRepository.findByUserId(userId, token)
                            .flatMap(actor -> 
                                organisationRepository.findByActorId(actor.getId(), token)
                                    .map(org -> UserProfileResponse.builder()
                                            .accessToken(authResponse.accessToken())
                                            .refreshToken(authResponse.refreshToken())
                                            .user(user)
                                            .actor(actor)
                                            .organisation(org)
                                            .build())
                                    .switchIfEmpty(Mono.just(UserProfileResponse.builder()
                                            .accessToken(authResponse.accessToken())
                                            .refreshToken(authResponse.refreshToken())
                                            .user(user)
                                            .actor(actor)
                                            .build()))
                            )
                            .switchIfEmpty(Mono.just(UserProfileResponse.builder()
                                    .accessToken(authResponse.accessToken())
                                    .refreshToken(authResponse.refreshToken())
                                    .user(user)
                                    .build()));
                });
    }
}
