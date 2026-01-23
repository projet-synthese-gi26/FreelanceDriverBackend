package com.yowyob.template.domain.ports.out;

import com.yowyob.template.infrastructure.adapters.outbound.external.dto.AuthResponse;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.RegisterRequest;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.TraMaSysUserResponse;
import reactor.core.publisher.Mono;

public interface AuthClientPort {
    Mono<AuthResponse> login(String identifier, String password);
    Mono<AuthResponse> registerUser(RegisterRequest request);
}
