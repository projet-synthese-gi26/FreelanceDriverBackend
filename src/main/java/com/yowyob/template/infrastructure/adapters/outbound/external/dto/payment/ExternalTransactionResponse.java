package com.yowyob.template.infrastructure.adapters.outbound.external.dto.payment;

import java.math.BigDecimal;
import java.util.UUID;

public record ExternalTransactionResponse(
    UUID id,
    UUID walletId,
    BigDecimal amount,
    String type,
    String status
) {}