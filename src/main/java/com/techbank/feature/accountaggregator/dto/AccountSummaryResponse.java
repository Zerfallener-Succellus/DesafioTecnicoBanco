package com.techbank.feature.accountaggregator.dto;

import java.math.BigDecimal;

public record AccountSummaryResponse(
        Long accountId,
        String accountNumber,
        String holderName,
        String type,
        BigDecimal availableBalance,
        BigDecimal blockedBalance,
        BigDecimal totalBalance
) {}