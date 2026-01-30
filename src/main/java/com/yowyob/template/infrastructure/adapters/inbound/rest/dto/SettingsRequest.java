package com.yowyob.template.infrastructure.adapters.inbound.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SettingsRequest(
        String theme,
        String language,
        Boolean longRideEnabled,
        Boolean shortRideEnabled,
        Boolean privacyEnable,
        Boolean allowCalls,
        Boolean allowMessages,
        Boolean notifyNewRides,
        Boolean notifyRatings,
        Boolean notifyPracticalTips,
        Boolean notifyPromotions,
        Boolean notifyPolicyUpdates,
        Boolean notifyPeakHourRecommendations,
        Boolean receiveEmail,
        Boolean receiveSms,
        Boolean receivePushNotifications,
        Boolean receiveWhatsapp) {
}