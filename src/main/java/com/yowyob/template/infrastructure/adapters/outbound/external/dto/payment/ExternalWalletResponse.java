package com.yowyob.template.infrastructure.adapters.outbound.external.dto.payment;

import java.math.BigDecimal;
import java.util.UUID;

public record ExternalWalletResponse(
    UUID id,
    UUID ownerId,
    String ownerName,
    BigDecimal balance
) {}