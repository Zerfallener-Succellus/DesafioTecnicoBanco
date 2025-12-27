package com.techbank.feature.accountaggregator.mapper;

import com.techbank.domain.account.Account;
import com.techbank.domain.balance.Balance;
import com.techbank.feature.accountaggregator.dto.AccountSummaryResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AccountMapper {

    public AccountSummaryResponse toSummary(Account account, Balance balance) {
        BigDecimal total = balance.getAvailableAmount().add(balance.getBlockedAmount());

        return new AccountSummaryResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getHolderName(),
                account.getType(),
                balance.getAvailableAmount(),
                balance.getBlockedAmount(),
                total
        );
    }
}