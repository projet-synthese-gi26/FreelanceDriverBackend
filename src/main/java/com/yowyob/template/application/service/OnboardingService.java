/*package com.yowyob.template.application.service;

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
        return ensureRoleNotExists(userId, "DRIVER")
                .then(resolveUserProfile(userId, request, accessToken))
                .flatMap(user -> onboardBusinessActor(user, "DRIVER", request.getOrganisationName(),
                        request.getOrganisationDescription(), request.getPhone(), request.getTitle(), request.getAddress(), accessToken, null));
    }

    public Mono<UserProfileResponse> becomeClient(UUID userId, com.yowyob.template.infrastructure.adapters.inbound.rest.dto.NewRoleRequest request, String accessToken) {
        return ensureRoleNotExists(userId, "CLIENT")
                .then(resolveUserProfile(userId, request, accessToken))
                .flatMap(user -> onboardBusinessActor(user, "CLIENT", request.getOrganisationName(),
                        request.getOrganisationDescription(), request.getPhone(), request.getTitle(), request.getAddress(), accessToken, null));
    }

    private Mono<User> resolveUserProfile(UUID userId, com.yowyob.template.infrastructure.adapters.inbound.rest.dto.NewRoleRequest request, String accessToken) {
        return actorRepository.findByUserId(userId, accessToken)
                .defaultIfEmpty(ClientRole.builder().build())
                .map(actor -> {
                    String displayName = actor.getDisplayName() != null ? actor.getDisplayName().trim() : "";
                    String[] names = displayName.split(" ", 2);
                    String fallbackFirstName = names.length > 0 ? names[0] : null;
                    String fallbackLastName = names.length > 1 ? names[1] : null;

                    String firstName = request.getFirstName() != null ? request.getFirstName() : fallbackFirstName;
                    String lastName = request.getLastName() != null ? request.getLastName() : fallbackLastName;
                    String email = request.getEmail() != null ? request.getEmail() : actor.getEmailAddress();

                    return User.builder()
                            .id(userId)
                            .email(email)
                            .firstName(firstName)
                            .lastName(lastName)
                            .build();
                });
    }

    private Mono<Void> ensureRoleNotExists(UUID userId, String roleType) {
        return actorRepository.findAll()
                .filter(actor -> userId.equals(actor.getUserId()))
                .filter(actor -> roleType.equalsIgnoreCase(actor.getRoleType()))
                .hasElements()
                .flatMap(exists -> exists
                        ? Mono.error(new IllegalStateException("User already has role: " + roleType))
                        : Mono.empty());
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


*/



package com.yowyob.template.application.service;

import com.yowyob.template.domain.model.*;
import com.yowyob.template.domain.ports.out.*;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.FullOnboardingRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.UserProfileResponse;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.RegisterRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.NewRoleRequest; // Assurez-vous d'importer ceci
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


//import pour le paiement

import com.yowyob.template.domain.event.DriverOnboardedEvent;
import lombok.extern.slf4j.Slf4j; // <--- LOGS
import org.springframework.context.ApplicationEventPublisher; // <--- IMPORT

import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class OnboardingService {
    private final AuthClientPort authClientPort;
    private final BusinessActorRepositoryPort actorRepository;
    private final OrganisationRepositoryPort organisationRepository;
    // 1. Ajout du repository utilisateur pour récupérer les infos existantes
    private final UserRepositoryPort userRepository; 
    
    //injection pour le paiement
    private final ApplicationEventPublisher publisher;

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
                })

                .doOnSuccess(response -> {
                        if ("DRIVER".equalsIgnoreCase(request.getRoleType())) {
                            log.info("Registration successful for DRIVER {}. Publishing wallet creation event.", response.getUser().getId());
                            publisher.publishEvent(new DriverOnboardedEvent(
                                    response.getUser().getId(),
                                    response.getUser().getEmail(),
                                    "DRIVER",
                                    response.getAccessToken() // Le token frais pour créer le wallet
                            ));
                        }
                    });
    
    }

    public Mono<UserProfileResponse> becomeDriver(UUID userId, NewRoleRequest request, String accessToken) {
        // Correction de la requête précédente incluse ici (passage de accessToken)
        return ensureRoleNotExists(userId, "DRIVER", accessToken)
                .then(resolveUserProfile(userId, request, accessToken))
                .flatMap(user -> {
                    // Si l'organisation n'a pas de nom, on en génère un par défaut basé sur le nom du user
                    String orgName = request.getOrganisationName() != null && !request.getOrganisationName().isBlank() 
                            ? request.getOrganisationName() 
                            : "Transport " + user.getLastName();
                    
                    return onboardBusinessActor(user, "DRIVER", orgName,
                        request.getOrganisationDescription(), 
                        user.getPhone(), // On utilise le téléphone résolu (soit du request, soit du user existant)
                        request.getTitle(), 
                        request.getAddress(), 
                        accessToken, 
                        null);
                })
                .doOnSuccess(response -> {
                        log.info("User {} became DRIVER. Publishing wallet creation event.", userId);
                        publisher.publishEvent(new DriverOnboardedEvent(
                                userId,
                                response.getUser().getEmail(),
                                "DRIVER",
                                accessToken
                        ));
                    });
    }

    public Mono<UserProfileResponse> becomeClient(UUID userId, NewRoleRequest request, String accessToken) {
        return ensureRoleNotExists(userId, "CLIENT", accessToken)
                .then(resolveUserProfile(userId, request, accessToken))
                .flatMap(user -> {
                    String orgName = request.getOrganisationName() != null && !request.getOrganisationName().isBlank()
                            ? request.getOrganisationName()
                            : "Espace Client " + user.getFirstName();

                    return onboardBusinessActor(user, "CLIENT", orgName,
                        request.getOrganisationDescription(), 
                        user.getPhone(), 
                        request.getTitle(), 
                        request.getAddress(), 
                        accessToken, 
                        null);
                });
    }

    // 2. Cette méthode récupère désormais le User réel depuis le service d'Auth
    // et fusionne avec les données de la requête si elles sont présentes.
    private Mono<User> resolveUserProfile(UUID userId, NewRoleRequest request, String accessToken) {
        return userRepository.findById(userId, accessToken)
                .map(existingUser -> {
                    // Logique de fusion (Merge):
                    // Si la requête contient une info, on l'utilise (mise à jour implicite).
                    // Sinon, on garde l'info existante du profil.
                    
                    String firstName = (request.getFirstName() != null && !request.getFirstName().isBlank()) 
                            ? request.getFirstName() 
                            : existingUser.getFirstName();

                    String lastName = (request.getLastName() != null && !request.getLastName().isBlank()) 
                            ? request.getLastName() 
                            : existingUser.getLastName();

                    String email = (request.getEmail() != null && !request.getEmail().isBlank()) 
                            ? request.getEmail() 
                            : existingUser.getEmail();
                    
                    String phone = (request.getPhone() != null && !request.getPhone().isBlank())
                            ? request.getPhone()
                            : existingUser.getPhone();

                    // On retourne un objet User mis à jour (mémoire) pour la création de l'acteur
                    return User.builder()
                            .id(userId)
                            .email(email)
                            .firstName(firstName)
                            .lastName(lastName)
                            .phone(phone) // Assurez-vous que le modèle User a bien ce champ
                            .build();
                });
    }

    private Mono<Void> ensureRoleNotExists(UUID userId, String roleType, String accessToken) {
        return actorRepository.findByUserId(userId, accessToken)
                .filter(actor -> roleType.equalsIgnoreCase(actor.getRoleType()))
                .hasElement()
                .flatMap(exists -> exists
                        ? Mono.error(new IllegalStateException("User already has role: " + roleType))
                        : Mono.empty());
    }

    private Mono<UserProfileResponse> onboardBusinessActor(User user, String roleType, String orgName, String orgDesc, String phone, String title, String addressString, String accessToken, String refreshToken) {
        // Create BusinessActor using the user details we merged earlier
        BusinessActor actor;
        if ("DRIVER".equalsIgnoreCase(roleType)) {
            actor = DriverRole.builder()
                    .userId(user.getId())
                    .displayName(user.getFirstName() + " " + user.getLastName())
                    .emailAddress(user.getEmail())
                    .phoneNumber(phone) // On passe le téléphone ici
                    .build();
        } else {
            actor = ClientRole.builder()
                    .userId(user.getId())
                    .displayName(user.getFirstName() + " " + user.getLastName())
                    .emailAddress(user.getEmail())
                    .phoneNumber(phone)
                    .build();
        }

        return actorRepository.save(actor, accessToken)
                .flatMap(savedActor -> {
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
                    
                    // Si une adresse est fournie dans la requête, on pourrait l'ajouter ici
                    // Note: L'entité OrganisationBuilder actuelle ne gère pas directement l'adresse string simple,
                    // il faudrait idéalement créer un objet Address si addressString n'est pas null.

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