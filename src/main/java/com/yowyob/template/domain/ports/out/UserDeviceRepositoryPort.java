package com.yowyob.template.domain.ports.out;

import com.yowyob.template.domain.model.UserDevice;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserDeviceRepositoryPort {
    Mono<UserDevice> upsert(UserDevice device);
    Flux<UserDevice> findByUserId(UUID userId);
    Mono<Void> deleteByToken(String fcmToken);
}
