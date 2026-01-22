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
        Settings toSave = Settings.builder()
                .id(settings.getId())
                .userId(settings.getUserId())
                .theme(settings.getTheme())
                .language(settings.getLanguage())
                .longRideEnabled(settings.getLongRideEnabled())
                .shortRideEnabled(settings.getShortRideEnabled())
                .privacyEnable(settings.getPrivacyEnable())
                .allowCalls(settings.getAllowCalls())
                .allowMessages(settings.getAllowMessages())
                .notifyNewRides(settings.getNotifyNewRides())
                .notifyRatings(settings.getNotifyRatings())
                .notifyPracticalTips(settings.getNotifyPracticalTips())
                .notifyPromotions(settings.getNotifyPromotions())
                .notifyPolicyUpdates(settings.getNotifyPolicyUpdates())
                .notifyPeakHourRecommendations(settings.getNotifyPeakHourRecommendations())
                .receiveEmail(settings.getReceiveEmail())
                .receiveSms(settings.getReceiveSms())
                .receivePushNotifications(settings.getReceivePushNotifications())
                .receiveWhatsapp(settings.getReceiveWhatsapp())
                .createdAt(settings.getCreatedAt() != null ? settings.getCreatedAt() : now)
                .updatedAt(settings.getUpdatedAt() != null ? settings.getUpdatedAt() : now)
                .build();
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
                    Settings updated = Settings.builder()
                            .id(id)
                            .userId(settings.getUserId() != null ? settings.getUserId() : existing.getUserId())
                            .theme(settings.getTheme() != null ? settings.getTheme() : existing.getTheme())
                            .language(settings.getLanguage() != null ? settings.getLanguage() : existing.getLanguage())
                            .longRideEnabled(settings.getLongRideEnabled() != null ? settings.getLongRideEnabled()
                                    : existing.getLongRideEnabled())
                            .shortRideEnabled(settings.getShortRideEnabled() != null ? settings.getShortRideEnabled()
                                    : existing.getShortRideEnabled())
                            .privacyEnable(settings.getPrivacyEnable() != null ? settings.getPrivacyEnable() : existing.getPrivacyEnable())
                            .allowCalls(settings.getAllowCalls() != null ? settings.getAllowCalls() : existing.getAllowCalls())
                            .allowMessages(settings.getAllowMessages() != null ? settings.getAllowMessages() : existing.getAllowMessages())
                            .notifyNewRides(settings.getNotifyNewRides() != null ? settings.getNotifyNewRides() : existing.getNotifyNewRides())
                            .notifyRatings(settings.getNotifyRatings() != null ? settings.getNotifyRatings() : existing.getNotifyRatings())
                            .notifyPracticalTips(settings.getNotifyPracticalTips() != null ? settings.getNotifyPracticalTips()
                                    : existing.getNotifyPracticalTips())
                            .notifyPromotions(settings.getNotifyPromotions() != null ? settings.getNotifyPromotions()
                                    : existing.getNotifyPromotions())
                            .notifyPolicyUpdates(settings.getNotifyPolicyUpdates() != null ? settings.getNotifyPolicyUpdates()
                                    : existing.getNotifyPolicyUpdates())
                            .notifyPeakHourRecommendations(settings.getNotifyPeakHourRecommendations() != null ? settings.getNotifyPeakHourRecommendations()
                                    : existing.getNotifyPeakHourRecommendations())
                            .receiveEmail(settings.getReceiveEmail() != null ? settings.getReceiveEmail() : existing.getReceiveEmail())
                            .receiveSms(settings.getReceiveSms() != null ? settings.getReceiveSms() : existing.getReceiveSms())
                            .receivePushNotifications(settings.getReceivePushNotifications() != null ? settings.getReceivePushNotifications()
                                    : existing.getReceivePushNotifications())
                            .receiveWhatsapp(settings.getReceiveWhatsapp() != null ? settings.getReceiveWhatsapp()
                                    : existing.getReceiveWhatsapp())
                            .createdAt(existing.getCreatedAt())
                            .updatedAt(new java.sql.Timestamp(System.currentTimeMillis()))
                            .build();
                    return repository.save(updated);
                });
    }

    @Override
    public Mono<Void> deleteSettings(UUID id) {
        return repository.deleteById(id);
    }
}
