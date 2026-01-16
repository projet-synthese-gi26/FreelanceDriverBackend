package com.yowyob.template.application.service;

import com.yowyob.template.domain.model.Settings;
import com.yowyob.template.domain.ports.in.createSettingsUseCase;
import com.yowyob.template.domain.ports.out.SettingsRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettingsService implements createSettingsUseCase {
    private final SettingsRepositoryPort repository;

    @Override
    public Mono<Settings> createSettings(Settings settings) {
        java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
        Settings toSave = new Settings(
                settings.id(),
                settings.userId(),
                settings.theme(),
                settings.language(),
                settings.longRideEnabled(),
                settings.shortRideEnabled(),
                settings.privacyEnable(),
                settings.allowCalls(),
                settings.allowMessages(),
                settings.notifyNewRides(),
                settings.notifyRatings(),
                settings.notifyPracticalTips(),
                settings.notifyPromotions(),
                settings.notifyPolicyUpdates(),
                settings.notifyPeakHourRecommendations(),
                settings.receiveEmail(),
                settings.receiveSms(),
                settings.receivePushNotifications(),
                settings.receiveWhatsapp(),
                settings.createdAt() != null ? settings.createdAt() : now,
                settings.updatedAt() != null ? settings.updatedAt() : now);
        return repository.save(toSave);
    }

    @Override
    public Mono<Settings> getSettingsById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Flux<Settings> getAllSettings() {
        return repository.findAll();
    }

    @Override
    public Mono<Settings> updateSettings(UUID id, Settings settings) {
        return repository.findById(id)
                .flatMap(existing -> {
                    Settings updated = new Settings(
                            id,
                            settings.userId() != null ? settings.userId() : existing.userId(),
                            settings.theme() != null ? settings.theme() : existing.theme(),
                            settings.language() != null ? settings.language() : existing.language(),
                            settings.longRideEnabled() != null ? settings.longRideEnabled()
                                    : existing.longRideEnabled(),
                            settings.shortRideEnabled() != null ? settings.shortRideEnabled()
                                    : existing.shortRideEnabled(),
                            settings.privacyEnable() != null ? settings.privacyEnable() : existing.privacyEnable(),
                            settings.allowCalls() != null ? settings.allowCalls() : existing.allowCalls(),
                            settings.allowMessages() != null ? settings.allowMessages() : existing.allowMessages(),
                            settings.notifyNewRides() != null ? settings.notifyNewRides() : existing.notifyNewRides(),
                            settings.notifyRatings() != null ? settings.notifyRatings() : existing.notifyRatings(),
                            settings.notifyPracticalTips() != null ? settings.notifyPracticalTips()
                                    : existing.notifyPracticalTips(),
                            settings.notifyPromotions() != null ? settings.notifyPromotions()
                                    : existing.notifyPromotions(),
                            settings.notifyPolicyUpdates() != null ? settings.notifyPolicyUpdates()
                                    : existing.notifyPolicyUpdates(),
                            settings.notifyPeakHourRecommendations() != null ? settings.notifyPeakHourRecommendations()
                                    : existing.notifyPeakHourRecommendations(),
                            settings.receiveEmail() != null ? settings.receiveEmail() : existing.receiveEmail(),
                            settings.receiveSms() != null ? settings.receiveSms() : existing.receiveSms(),
                            settings.receivePushNotifications() != null ? settings.receivePushNotifications()
                                    : existing.receivePushNotifications(),
                            settings.receiveWhatsapp() != null ? settings.receiveWhatsapp()
                                    : existing.receiveWhatsapp(),
                            settings.createdAt() != null ? settings.createdAt() : existing.createdAt(),
                            settings.updatedAt() != null ? settings.updatedAt() : existing.updatedAt());
                    return repository.save(updated);
                });
    }

    @Override
    public Mono<Void> deleteSettings(UUID id) {
        return repository.deleteById(id);
    }
}
