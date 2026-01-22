package com.yowyob.template.domain.ports.in;

import com.yowyob.template.infrastructure.adapters.outbound.external.dto.AuthResponse;
import reactor.core.publisher.Mono;

public interface AuthUseCase {
    Mono<AuthResponse> login(String identifier, String password);
}
