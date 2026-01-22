package com.yowyob.template.infrastructure.adapters.inbound.rest;

import com.yowyob.template.domain.ports.in.AuthUseCase;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.LoginRequest;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Endpoints pour l'authentification des utilisateurs")
public class AuthController {
    private final AuthUseCase authUseCase;

    @PostMapping("/login")
    @Operation(summary = "Connexion utilisateur", description = "Permet à un utilisateur de se connecter avec son identifiant et son mot de passe. Retourne un token JWT.")
    public Mono<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return authUseCase.login(request.identifier(), request.password());
    }
}
