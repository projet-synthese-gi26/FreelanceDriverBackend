package com.yowyob.template.infrastructure.adapters.inbound.rest;

import com.yowyob.template.application.service.UserDeviceService;
import com.yowyob.template.domain.model.UserDevice;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.UserDeviceRegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user-devices")
@RequiredArgsConstructor
@Tag(name = "User Devices", description = "Gestion des appareils utilisateurs (FCM tokens)")
public class UserDeviceController {

    private final UserDeviceService service;

    @PostMapping
    @Operation(summary = "Enregistrer/mettre à jour un appareil", description = "Enregistre le token FCM de l'utilisateur connecté.")
    public Mono<UserDevice> register(
            @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
            @Valid @RequestBody UserDeviceRegisterRequest request
    ) {
        UUID userId = UUID.fromString(principal.getAttribute("sub"));
        return service.registerDevice(userId, request.fcmToken(), request.platform());
    }

    @GetMapping("/me")
    @Operation(summary = "Lister mes appareils", description = "Liste les appareils (tokens) de l'utilisateur connecté.")
    public Flux<UserDevice> me(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        UUID userId = UUID.fromString(principal.getAttribute("sub"));
        return service.getUserDevices(userId);
    }

    @DeleteMapping("/token/{token}")
    @Operation(summary = "Supprimer un token", description = "Supprime un token FCM (logout/uninstall).")
    public Mono<Void> delete(@PathVariable("token") String token) {
        return service.deleteByToken(token);
    }
}
