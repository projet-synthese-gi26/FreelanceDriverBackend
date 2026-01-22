package com.yowyob.template.application.service;

import com.yowyob.template.domain.model.*;
import com.yowyob.template.domain.ports.out.*;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.FullOnboardingRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.UserProfileResponse;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.RegisterRequest;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.TraMaSysUserResponse;
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
    private final ContactRepositoryPort contactRepository;
    private final AddressRepositoryPort addressRepository;
    private final SettingsRepositoryPort settingsRepository;

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
                .flatMap(externalUser -> {
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
                            request.getOrganisationDescription(), request.getPhone(), request.getTitle(), request.getAddress());
                });
    }

    public Mono<UserProfileResponse> becomeDriver(UUID userId, com.yowyob.template.infrastructure.adapters.inbound.rest.dto.NewRoleRequest request) {
        User user = User.builder()
                .id(userId)
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();
        return onboardBusinessActor(user, "DRIVER", request.getOrganisationName(),
                request.getOrganisationDescription(), request.getPhone(), request.getTitle(), request.getAddress());
    }

    public Mono<UserProfileResponse> becomeClient(UUID userId, com.yowyob.template.infrastructure.adapters.inbound.rest.dto.NewRoleRequest request) {
        User user = User.builder()
                .id(userId)
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();
        return onboardBusinessActor(user, "CLIENT", request.getOrganisationName(),
                request.getOrganisationDescription(), request.getPhone(), request.getTitle(), request.getAddress());
    }

    private Mono<UserProfileResponse> onboardBusinessActor(User user, String roleType, String orgName, String orgDesc, String phone, String title, String addressString) {
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

        return actorRepository.save(actor)
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

                    return organisationRepository.save(organisation)
                            .flatMap(savedOrg -> {
                                // 4. Create Contact
                                Contact contact = Contact.builder()
                                        .contactableId(savedOrg.getId())
                                        .contactableType("ORGANIZATION")
                                        .firstName(user.getFirstName())
                                        .lastName(user.getLastName())
                                        .email(user.getEmail())
                                        .phoneNumber(phone)
                                        .title(title != null ? title : "Owner")
                                        .isFavorite(true)
                                        .build();

                                // 5. Create Address
                                Address address = Address.builder()
                                        .addressableId(savedOrg.getId())
                                        .addressableType("ORGANIZATION")
                                        .type("HEADQUARTER")
                                        .city(addressString != null ? addressString : "Douala")
                                        .addressLine1("Unknown")
                                        .isDefault(true)
                                        .countryId(user.getId())
                                        .build();

                                // 6. Create Settings
                                Settings settings = Settings.builder()
                                        .userId(user.getId().toString())
                                        .theme("light")
                                        .notificationsEnabled(true)
                                        .build();

                                return Mono.zip(
                                        contactRepository.save(contact),
                                        addressRepository.save(address),
                                        settingsRepository.save(settings)
                                ).map(tuple -> UserProfileResponse.builder()
                                        .user(user)
                                        .actor(savedActor)
                                        .organisation(savedOrg)
                                        .build());
                            });
                });
    }
}
