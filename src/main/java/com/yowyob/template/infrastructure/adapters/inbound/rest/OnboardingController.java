package com.yowyob.template.infrastructure.adapters.inbound.rest;

import com.yowyob.template.application.service.OnboardingService;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.FullOnboardingRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.NewRoleRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Onboarding", description = "Gestion de l'inscription et de l'évolution des rôles (Driver/Client)")
public class OnboardingController {
    private final OnboardingService onboardingService;

    @PostMapping("/register")
    @Operation(summary = "Inscription complète", description = "Crée un nouvel utilisateur, son acteur métier (Chauffeur ou Client), son organisation, ses contacts et ses paramètres par défaut.")
    public Mono<UserProfileResponse> register(@RequestBody FullOnboardingRequest request) {
        return onboardingService.registerAndOnboard(request);
    }

    @PostMapping("/become-driver")
    @Operation(summary = "Devenir Chauffeur", description = "Permet à un utilisateur existant de créer un profil Chauffeur et une organisation associée.")
    public Mono<UserProfileResponse> becomeDriver(@RequestParam UUID userId, 
                                                  @RequestBody NewRoleRequest request, 
                                                  @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        return onboardingService.becomeDriver(userId, request, token);
    }

    @PostMapping("/become-client")
    @Operation(summary = "Devenir Client", description = "Permet à un utilisateur existant de créer un profil Client et une organisation associée.")
    public Mono<UserProfileResponse> becomeClient(@RequestParam UUID userId, 
                                                  @RequestBody NewRoleRequest request, 
                                                  @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        return onboardingService.becomeClient(userId, request, token);
    }
}
