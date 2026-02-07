package com.yowyob.template.application.service;

import com.yowyob.template.domain.model.DevicePlatform;
import com.yowyob.template.domain.model.UserDevice;
import com.yowyob.template.domain.ports.out.UserDeviceRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDeviceService {

    private final UserDeviceRepositoryPort repository;

    public Mono<UserDevice> registerDevice(UUID userId, String fcmToken, DevicePlatform platform) {
        UserDevice device = UserDevice.builder()
                .userId(userId)
                .fcmToken(fcmToken)
                .platform(platform)
                .build();
        return repository.upsert(device);
    }

    public Flux<UserDevice> getUserDevices(UUID userId) {
        return repository.findByUserId(userId);
    }

    public Mono<Void> deleteByToken(String fcmToken) {
        return repository.deleteByToken(fcmToken);
    }
}
