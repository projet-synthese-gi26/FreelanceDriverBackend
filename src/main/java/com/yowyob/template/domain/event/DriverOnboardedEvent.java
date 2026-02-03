package com.yowyob.template.domain.event;

import java.util.UUID;

public record DriverOnboardedEvent(
    UUID userId,
    String email,
    String role,
    String token // Nécessaire pour appeler l'API externe sécurisée
) {}