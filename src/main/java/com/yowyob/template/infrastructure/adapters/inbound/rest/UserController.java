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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Gestion des utilisateurs (Profil, Mot de passe, Photo)")
public class UserController {

    private final UserUseCase userUseCase;

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour le profil utilisateur", description = "Met à jour le prénom, nom ou téléphone.")
    public Mono<User> updateProfile(@PathVariable UUID id, @org.springframework.web.bind.annotation.RequestBody UpdateUserProfileRequest request) {
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .build();
        return userUseCase.updateUserProfile(id, user);
    }

    @PutMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Changer le mot de passe", description = "Met à jour le mot de passe utilisateur. Nécessite l'ancien mot de passe.")
    public Mono<Void> changePassword(@PathVariable UUID id, @org.springframework.web.bind.annotation.RequestBody ChangePasswordRequest request) {
        return userUseCase.changePassword(id, request.getCurrentPassword(), request.getNewPassword());
    }

    @PostMapping(value = "/{id}/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Mettre à jour la photo de profil", description = "Télécharge ou remplace la photo de profil de l'utilisateur.")
    public Mono<User> updateProfilePicture(@PathVariable UUID id, @RequestPart("file") FilePart file) {
        return userUseCase.updateProfilePicture(id, file);
    }
}
