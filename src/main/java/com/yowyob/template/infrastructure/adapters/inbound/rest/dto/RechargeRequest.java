package com.yowyob.template.infrastructure.adapters.inbound.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record RechargeRequest(
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "10", message = "Minimum recharge amount is 100")
    BigDecimal amount
) {}