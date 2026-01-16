package com.yowyob.template.infrastructure.adapters.outbound.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.sql.Timestamp;
import java.util.UUID;

@Table("settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettingsEntity {
    @Id
    private UUID id;
    private String userId;
    private String theme;
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
