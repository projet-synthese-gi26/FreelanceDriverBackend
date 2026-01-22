package com.yowyob.template.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Settings {
    private UUID id;
    private String userId;
    private String theme;
    private Boolean notificationsEnabled;
    private String language;
    private Boolean longRideEnabled;
    private Boolean shortRideEnabled;
    private Boolean privacyEnable;
    private Boolean allowCalls;
    private Boolean allowMessages;
    private Boolean notifyNewRides;
    private Boolean notifyRatings;
    private Boolean notifyPracticalTips;
    private Boolean notifyPromotions;
    private Boolean notifyPolicyUpdates;
    private Boolean notifyPeakHourRecommendations;
    private Boolean receiveEmail;
    private Boolean receiveSms;
    private Boolean receivePushNotifications;
    private Boolean receiveWhatsapp;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
