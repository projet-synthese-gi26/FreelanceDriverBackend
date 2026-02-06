package com.yowyob.template.infrastructure.adapters.outbound.external.dto.notification;

//import java.util.UUID;

public record ServiceRegistrationResponse(
    Object serviceId, // On utilise Object ou Integer pour être flexible avec l'API externe
    String token,
    String name
) {}