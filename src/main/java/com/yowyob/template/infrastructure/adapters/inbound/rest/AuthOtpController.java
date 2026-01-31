package com.yowyob.template.infrastructure.adapters.inbound.rest;

import com.yowyob.template.application.service.AuthRegistrationService;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.OtpRegisterVerifyRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.RegisterInitRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.RegisterInitResponse;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "OTP Verification", description = "Vérification OTP pour l'inscription")
public class AuthOtpController {
    private final AuthRegistrationService registrationService;

    @PostMapping("/register-init")
    @Operation(summary = "Initialiser l'inscription", description = "Envoie un OTP par email pour démarrer l'inscription.")
    public Mono<ResponseEntity<RegisterInitResponse>> registerInit(@Valid @RequestBody RegisterInitRequest request) {
        log.info("Register init requested for email={}", request.email());
        return registrationService.registerInit(request)
                .thenReturn(ResponseEntity.ok(RegisterInitResponse.builder()
                        .success(true)
                        .message("OTP sent to email")
                        .build()))
                .onErrorResume(IllegalArgumentException.class, ex -> Mono.just(ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(RegisterInitResponse.builder()
                                .success(false)
                                .message(ex.getMessage())
                                .build())));
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Vérifier OTP", description = "Vérifie un code OTP et finalise l'inscription.")
    public Mono<ResponseEntity<UserProfileResponse>> verifyOtp(@Valid @RequestBody OtpRegisterVerifyRequest request) {
        log.info("OTP verification requested for email={}", request.email());
        return registrationService.verifyOtpAndRegister(request)
                .map(ResponseEntity::ok)
                .onErrorResume(IllegalArgumentException.class, ex -> Mono.just(ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .build()));
    }
}
