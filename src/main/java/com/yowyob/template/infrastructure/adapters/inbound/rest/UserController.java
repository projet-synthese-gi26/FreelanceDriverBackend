package com.yowyob.template.infrastructure.adapters.inbound.rest;

import com.yowyob.template.domain.model.User;
import com.yowyob.template.domain.ports.in.UserUseCase;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.ChangePasswordRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.UpdateUserProfileRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Gestion des utilisateurs (Profil, Mot de passe, Photo)")
public class UserController {

    private final UserUseCase userUseCase;

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir le profil utilisateur public", description = "Récupère les informations publiques d'un utilisateur par son ID (endpoint public).")
    public Mono<User> getPublicUser(@PathVariable UUID id) {
        return userUseCase.getUserById(id);
    }

    @PutMapping
    @Operation(summary = "Mettre à jour le profil utilisateur", description = "Met à jour le prénom, nom ou téléphone.")
    public Mono<User> updateProfile(
            @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
            ServerWebExchange exchange,
            @org.springframework.web.bind.annotation.RequestBody UpdateUserProfileRequest request
    ) {
        UUID id = UUID.fromString(principal.getAttribute("sub"));
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .build();
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        return userUseCase.updateUserProfile(id, user, authHeader);
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Changer le mot de passe", description = "Met à jour le mot de passe utilisateur. Nécessite l'ancien mot de passe.")
    public Mono<Void> changePassword(
            @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
            ServerWebExchange exchange,
            @org.springframework.web.bind.annotation.RequestBody ChangePasswordRequest request
    ) {
        UUID id = UUID.fromString(principal.getAttribute("sub"));
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        return userUseCase.changePassword(id, request.getCurrentPassword(), request.getNewPassword(), authHeader);
    }

    @PostMapping(value = "/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Mettre à jour la photo de profil", description = "Télécharge ou remplace la photo de profil de l'utilisateur.")
    public Mono<User> updateProfilePicture(
            @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
            ServerWebExchange exchange,
            @RequestPart("file") FilePart file
    ) {
        UUID id = UUID.fromString(principal.getAttribute("sub"));
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        return userUseCase.updateProfilePicture(id, file, authHeader);
    }
}
