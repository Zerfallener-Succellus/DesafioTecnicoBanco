package com.techbank.feature.accountaggregator.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AccountCreateRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Deve passar quando todos os campos são válidos (CORRENTE)")
    void shouldValidateCorrectlyCorrente() {
        var request = new AccountCreateRequest("12345", "Fulano de Tal", "CORRENTE");

        Set<ConstraintViolation<AccountCreateRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve passar quando todos os campos são válidos (POUPANCA)")
    void shouldValidateCorrectlyPoupanca() {
        var request = new AccountCreateRequest("99999", "Ciclano", "POUPANCA");

        Set<ConstraintViolation<AccountCreateRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve falhar quando AccountNumber está vazio")
    void shouldFailWhenAccountNumberIsBlank() {
        var request = new AccountCreateRequest("", "Fulano", "CORRENTE");

        Set<ConstraintViolation<AccountCreateRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals("O num da conta é obrigatório", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Deve falhar quando HolderName é nulo")
    void shouldFailWhenHolderNameIsNull() {
        var request = new AccountCreateRequest("123", null, "CORRENTE");

        Set<ConstraintViolation<AccountCreateRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals("O nome do titular é obrigatório", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Deve falhar quando o Tipo não é CORRENTE nem POUPANCA")
    void shouldFailWhenTypeIsInvalid() {
        var request = new AccountCreateRequest("123", "Fulano", "INVESTIMENTO");

        Set<ConstraintViolation<AccountCreateRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        ConstraintViolation<AccountCreateRequest> violation = violations.iterator().next();

        assertEquals("deve ser CORRENTE ou POUPANCA", violation.getMessage());
        assertEquals("type", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Deve falhar quando o Tipo está minúsculo")
    void shouldFailWhenTypeIsLowerCase() {
        var request = new AccountCreateRequest("123", "Fulano", "corrente");

        Set<ConstraintViolation<AccountCreateRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals("deve ser CORRENTE ou POUPANCA", violations.iterator().next().getMessage());
    }
}