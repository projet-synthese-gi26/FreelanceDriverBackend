package com.yowyob.template.application.service;

import com.yowyob.template.domain.ports.in.AuthUseCase;
import com.yowyob.template.domain.ports.out.AuthClientPort;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {
    private final AuthClientPort authClientPort;

    @Override
    public Mono<AuthResponse> login(String identifier, String password) {
        return authClientPort.login(identifier, password);
    }
}
