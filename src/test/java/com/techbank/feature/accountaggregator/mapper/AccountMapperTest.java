package com.techbank.feature.accountaggregator.mapper;

import com.techbank.domain.account.Account;
import com.techbank.domain.balance.Balance;
import com.techbank.feature.accountaggregator.dto.AccountSummaryResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class AccountMapperTest {

    private final AccountMapper mapper = new AccountMapper();

    @Test
    void shouldCalculateTotalBalanceCorrectly() {

        Account account = new Account("123", "Silva", "CORRENTE");
        account.setId(1L);

        Balance balance = new Balance(1L, new BigDecimal("100.00"), new BigDecimal("50.00"));

        AccountSummaryResponse response = mapper.toSummary(account, balance);

        Assertions.assertEquals(new BigDecimal("100.00"), response.availableBalance());
        Assertions.assertEquals(new BigDecimal("50.00"), response.blockedBalance());

        Assertions.assertEquals(new BigDecimal("150.00"), response.totalBalance());
    }
}