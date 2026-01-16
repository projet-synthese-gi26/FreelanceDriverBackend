package com.yowyob.template.infrastructure.adapters.inbound.rest.dto;

import java.util.UUID;

public record BusinessActorResponse(
    UUID id,
    String userId,
    String name,
    String phoneNumber,
    String emailAddress
) {}