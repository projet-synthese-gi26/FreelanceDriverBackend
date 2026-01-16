package com.yowyob.template.domain.ports.out;

import com.yowyob.template.domain.model.Settings;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SettingsRepositoryPort {
    Mono<Settings> save(Settings settings);
    Mono<Settings> findById(UUID id);
    Flux<Settings> findAll();
    Mono<Void> deleteById(UUID id);
    Mono<Void> deleteAll();
}
