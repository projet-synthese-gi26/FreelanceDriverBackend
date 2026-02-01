package com.yowyob.template.infrastructure.adapters.outbound.external.dto.payment;

import java.math.BigDecimal;
import java.util.UUID;

public record ExternalTransactionRequest(
    UUID walletId,
    BigDecimal amount,
    String type // "RECHARGE" ou "PAYMENT"
) {}