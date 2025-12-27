package com.techbank.feature.accountaggregator;

import com.techbank.domain.account.Account;
import com.techbank.domain.account.AccountRepository;
import com.techbank.domain.balance.Balance;
import com.techbank.domain.balance.BalanceRepository;
import com.techbank.feature.accountaggregator.dto.AccountCreateRequest;
import com.techbank.feature.accountaggregator.dto.AccountSummaryResponse;
import com.techbank.feature.accountaggregator.mapper.AccountMapper;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountAggregatorService {

    private final AccountRepository accountRepo;
    private final BalanceRepository balanceRepo;
    private final AccountMapper mapper;


    @Timed(value = "aggregation.time", description = "Tempo gasto consolidando contas e saldos")
    public List<AccountSummaryResponse> listAllAccounts() {
        log.info("A iniciar a agregação de contas e saldos...");


        List<Account> accounts = accountRepo.findAll();
        if (accounts.isEmpty()) return List.of();


        List<Long> accountIds = accounts.stream().map(Account::getId).toList();


        List<Balance> balances = balanceRepo.findByAccountIdIn(accountIds);


        Map<Long, Balance> balanceMap = balances.stream()
                .collect(Collectors.toMap(Balance::getAccountId, Function.identity()));


        return accounts.stream().map(acc -> {
            Balance bal = balanceMap.getOrDefault(acc.getId(),
                    new Balance(acc.getId(), BigDecimal.ZERO, BigDecimal.ZERO));

            return mapper.toSummary(acc, bal);
        }).toList();
    }


    public AccountSummaryResponse createAccount(AccountCreateRequest request) {

        Account account = new Account(request.accountNumber(), request.holderName(), request.type());
        Account savedAccount = accountRepo.save(account);


        Balance initialBalance = new Balance(savedAccount.getId(), BigDecimal.ZERO, BigDecimal.ZERO);

        try {
            balanceRepo.save(initialBalance);
        } catch (Exception e) {
            accountRepo.deleteById(savedAccount.getId());
            throw new RuntimeException("Falha ao criar saldo a operação fez rolback.");
        }

        return mapper.toSummary(savedAccount, initialBalance);
    }
}