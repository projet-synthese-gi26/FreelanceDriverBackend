package com.yowyob.template.infrastructure.adapters.inbound.rest.dto;

import java.sql.Timestamp;
import java.util.UUID;

public record SettingsResponse(
    UUID id,
    String userId,
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
    Boolean receiveWhatsapp,
    Timestamp createdAt,
    Timestamp updatedAt
) {}