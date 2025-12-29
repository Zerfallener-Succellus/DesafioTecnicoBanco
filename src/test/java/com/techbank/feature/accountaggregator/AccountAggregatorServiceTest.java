package com.techbank.feature.accountaggregator;

import com.techbank.domain.account.Account;
import com.techbank.domain.account.AccountRepository;
import com.techbank.domain.balance.Balance;
import com.techbank.domain.balance.BalanceRepository;
import com.techbank.feature.accountaggregator.dto.AccountCreateRequest;
import com.techbank.feature.accountaggregator.dto.AccountSummaryResponse;
import com.techbank.feature.accountaggregator.mapper.AccountMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountAggregatorServiceTest {

    @InjectMocks
    private AccountAggregatorService service;

    @Mock
    private AccountRepository accountRepo;

    @Mock
    private BalanceRepository balanceRepo;

    @Mock
    private AccountMapper mapper;

    @Test
    @DisplayName("Deve retornar lista vazia imediatamente se não houver contas")
    void shouldReturnEmptyListWhenNoAccounts() {
        when(accountRepo.findAll()).thenReturn(Collections.emptyList());

        List<AccountSummaryResponse> result = service.listAllAccounts();

        assertTrue(result.isEmpty());
        verify(balanceRepo, never()).findByAccountIdIn(anyList());
        verify(mapper, never()).toSummary(any(), any());
    }

    @Test
    @DisplayName("Deve agregar contas e saldos corretamente")
    void shouldAggregateAccountsAndBalances() {
        Account account = new Account("123", "Fulano", "CORRENTE");
        account.setId(1L);
        Balance balance = new Balance(1L, BigDecimal.TEN, BigDecimal.ZERO);
        AccountSummaryResponse expectedResponse = new AccountSummaryResponse(
                1L, "123", "Fulano", "CORRENTE", BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.TEN
        );

        when(accountRepo.findAll()).thenReturn(List.of(account));
        when(balanceRepo.findByAccountIdIn(List.of(1L))).thenReturn(List.of(balance));
        when(mapper.toSummary(account, balance)).thenReturn(expectedResponse);

        List<AccountSummaryResponse> result = service.listAllAccounts();

        assertEquals(1, result.size());
        assertEquals(expectedResponse, result.get(0));
    }

    @Test
    @DisplayName("Deve usar saldo zerado se a conta não tiver registro na base de saldos")
    void shouldHandleMissingBalanceGracefully() {
        Account account = new Account("123", "Sem Saldo", "POUPANCA");
        account.setId(2L);

        when(accountRepo.findAll()).thenReturn(List.of(account));
        when(balanceRepo.findByAccountIdIn(List.of(2L))).thenReturn(Collections.emptyList());

        service.listAllAccounts();

        verify(mapper).toSummary(eq(account), argThat(b ->
                b.getAccountId().equals(2L) &&
                        b.getAvailableAmount().equals(BigDecimal.ZERO) &&
                        b.getBlockedAmount().equals(BigDecimal.ZERO)
        ));
    }

    @Test
    @DisplayName("Deve criar conta e saldo inicial com sucesso")
    void shouldCreateAccountSuccessfully() {
        AccountCreateRequest request = new AccountCreateRequest("123", "Novo", "CORRENTE");
        Account savedAccount = new Account("123", "Novo", "CORRENTE");
        savedAccount.setId(10L);

        AccountSummaryResponse expected = new AccountSummaryResponse(
                10L, "123", "Novo", "CORRENTE", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
        );

        when(accountRepo.save(any(Account.class))).thenReturn(savedAccount);
        when(mapper.toSummary(eq(savedAccount), any(Balance.class))).thenReturn(expected);

        AccountSummaryResponse result = service.createAccount(request);

        assertNotNull(result);
        verify(balanceRepo).save(argThat(b ->
                b.getAccountId().equals(10L) &&
                        b.getAvailableAmount().equals(BigDecimal.ZERO)
        ));
    }

    @Test
    @DisplayName("Deve fazer rollback se falhar ao salvar saldo")
    void shouldRollbackWhenBalanceSaveFails() {
        AccountCreateRequest request = new AccountCreateRequest("123", "Erro", "CORRENTE");
        Account savedAccount = new Account("123", "Erro", "CORRENTE");
        savedAccount.setId(99L);

        when(accountRepo.save(any(Account.class))).thenReturn(savedAccount);
        doThrow(new RuntimeException("Erro banco B")).when(balanceRepo).save(any(Balance.class));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.createAccount(request));

        assertEquals("Falha ao criar saldo a operação fez rolback.", ex.getMessage());

        verify(accountRepo).deleteById(99L);
    }
}