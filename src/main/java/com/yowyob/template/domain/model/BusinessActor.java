package com.yowyob.template.domain.model;

import java.util.UUID;

public record BusinessActor(
    UUID id,
    String userId,
    String name,
    String phoneNumber,
    String emailAddress
){}