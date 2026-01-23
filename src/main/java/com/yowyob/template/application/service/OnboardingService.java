package com.yowyob.template.application.service;

import com.yowyob.template.domain.model.*;
import com.yowyob.template.domain.ports.out.*;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.FullOnboardingRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.UserProfileResponse;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.RegisterRequest;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OnboardingService {
    private final AuthClientPort authClientPort;
    private final BusinessActorRepositoryPort actorRepository;
    private final OrganisationRepositoryPort organisationRepository;

    public Mono<UserProfileResponse> registerAndOnboard(FullOnboardingRequest request) {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(request.getPassword())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .service("LETS_GO")
                .roles(java.util.List.of(request.getRoleType()))
                .build();

        return authClientPort.registerUser(registerRequest)
                .flatMap(authResponse -> {
                    var externalUser = authResponse.user();
                    UUID userId = UUID.fromString(externalUser.id());
                    User user = User.builder()
                            .id(userId)
                            .email(externalUser.email())
                            .username(externalUser.username())
                            .firstName(externalUser.firstName())
                            .lastName(externalUser.lastName())
                            .photoUri(externalUser.photoUri())
                            .build();

                    return onboardBusinessActor(user, request.getRoleType(), request.getOrganisationName(),
                            request.getOrganisationDescription(), request.getPhone(), request.getTitle(), request.getAddress(), authResponse.accessToken(), authResponse.refreshToken());
                });
    }

    public Mono<UserProfileResponse> becomeDriver(UUID userId, com.yowyob.template.infrastructure.adapters.inbound.rest.dto.NewRoleRequest request, String accessToken) {
        User user = User.builder()
                .id(userId)
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();
        return onboardBusinessActor(user, "DRIVER", request.getOrganisationName(),
                request.getOrganisationDescription(), request.getPhone(), request.getTitle(), request.getAddress(), accessToken, null);
    }

    public Mono<UserProfileResponse> becomeClient(UUID userId, com.yowyob.template.infrastructure.adapters.inbound.rest.dto.NewRoleRequest request, String accessToken) {
        User user = User.builder()
                .id(userId)
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();
        return onboardBusinessActor(user, "CLIENT", request.getOrganisationName(),
                request.getOrganisationDescription(), request.getPhone(), request.getTitle(), request.getAddress(), accessToken, null);
    }

    private Mono<UserProfileResponse> onboardBusinessActor(User user, String roleType, String orgName, String orgDesc, String phone, String title, String addressString, String accessToken, String refreshToken) {
        // 2. Create BusinessActor
        BusinessActor actor;
        if ("DRIVER".equalsIgnoreCase(roleType)) {
            actor = DriverRole.builder()
                    .userId(user.getId())
                    .displayName(user.getFirstName() + " " + user.getLastName())
                    .emailAddress(user.getEmail())
                    .build();
        } else {
            actor = ClientRole.builder()
                    .userId(user.getId())
                    .displayName(user.getFirstName() + " " + user.getLastName())
                    .emailAddress(user.getEmail())
                    .build();
        }

        return actorRepository.save(actor, accessToken)
                .flatMap(savedActor -> {
                    // 3. Create Organisation
                    OrganisationBuilder builder = new OrganisationBuilder()
                            .withName(orgName)
                            .withActorId(savedActor.getId());

                    if (savedActor instanceof DriverRole) {
                        builder.asDriver();
                    } else {
                        builder.asClient();
                    }

                    Organisation organisation = builder.build();
                    organisation.setDescription(orgDesc);
                    organisation.setEmail(user.getEmail());
                    organisation.setService(savedActor instanceof DriverRole ? "LETS_GO" : "IT_SERVICES");

                    return organisationRepository.save(organisation, accessToken)
                            .map(savedOrg -> UserProfileResponse.builder()
                                    .accessToken(accessToken)
                                    .refreshToken(refreshToken)
                                    .user(user)
                                    .actor(savedActor)
                                    .organisation(savedOrg)
                                    .build());
                });
    }
}
