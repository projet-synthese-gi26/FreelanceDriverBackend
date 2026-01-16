package com.yowyob.template.domain.ports.in;

import com.yowyob.template.domain.model.Settings;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface createSettingsUseCase {
    Mono<Settings> createSettings(Settings settings);
    Mono<Settings> getSettingsById(UUID id);
    Flux<Settings> getAllSettings();
    Mono<Settings> updateSettings(UUID id, Settings settings);
    Mono<Void> deleteSettings(UUID id);
}
