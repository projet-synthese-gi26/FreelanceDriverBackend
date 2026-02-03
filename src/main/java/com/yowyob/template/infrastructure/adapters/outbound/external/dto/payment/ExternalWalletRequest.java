package com.yowyob.template.infrastructure.adapters.outbound.external.dto.payment;

import java.util.UUID;

public record ExternalWalletRequest(
    UUID ownerId,
    String ownerName
) {}