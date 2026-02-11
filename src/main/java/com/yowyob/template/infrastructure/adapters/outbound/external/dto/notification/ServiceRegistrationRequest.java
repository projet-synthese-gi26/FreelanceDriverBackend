package com.yowyob.template.infrastructure.adapters.outbound.external.dto.notification;

import lombok.Builder;

@Builder
public record ServiceRegistrationRequest(
    String name,
    String emailServerHost,
    Integer emailServerPort,
    String emailUsername,
    String emailPassword,
    String smsServerHost,
    String smsServerPort,
    String smstoken,
    String whatsappApiUrl,
    String whatsappIdInstance,
    String whatsappApiTokenInstance,
    String firebaseServiceAccountJson
) {}