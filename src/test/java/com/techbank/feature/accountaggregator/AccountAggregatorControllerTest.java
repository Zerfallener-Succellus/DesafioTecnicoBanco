package com.techbank.feature.accountaggregator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techbank.feature.accountaggregator.dto.AccountCreateRequest;
import com.techbank.feature.accountaggregator.dto.AccountSummaryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountAggregatorController.class)
class AccountAggregatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountAggregatorService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve retornar 200 OK e a lista de contas")
    void shouldReturnListAccounts() throws Exception {
        AccountSummaryResponse account = new AccountSummaryResponse(
                1L, "12345", "Fulano", "CORRENTE",
                new BigDecimal("100.00"), new BigDecimal("0.00"), new BigDecimal("100.00")
        );

        when(service.listAllAccounts()).thenReturn(List.of(account));

        mockMvc.perform(get("/accounts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].accountNumber", is("12345")))
                .andExpect(jsonPath("$[0].holderName", is("Fulano")));
    }

    @Test
    @DisplayName("Deve retornar 200 OK e lista vazia quando não houver registros")
    void shouldReturnEmptyList() throws Exception {
        when(service.listAllAccounts()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/accounts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Deve retornar 201 Created e Location Header ao criar conta com sucesso")
    void shouldCreateAccountSuccessfully() throws Exception {
        AccountCreateRequest request = new AccountCreateRequest("12345", "Fulano", "CORRENTE");
        AccountSummaryResponse response = new AccountSummaryResponse(
                1L, "12345", "Fulano", "CORRENTE",
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
        );

        when(service.createAccount(any(AccountCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/accounts/1"))
                .andExpect(jsonPath("$.accountId", is(1)))
                .andExpect(jsonPath("$.holderName", is("Fulano")));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando payload for inválido")
    void shouldReturnBadRequestWhenInvalid() throws Exception {
        AccountCreateRequest invalidRequest = new AccountCreateRequest("", "", "INVALIDO");

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}