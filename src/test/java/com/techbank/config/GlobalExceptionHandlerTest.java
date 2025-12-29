package com.techbank.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @Test
    @DisplayName("Deve retornar 400 Bad Request e detalhes formatados quando houver erro de validação")
    void handleValidationErrors_ShouldReturnBadRequest() {
        FieldError fieldError1 = new FieldError("account", "accountNumber", "não pode estar vazio");
        FieldError fieldError2 = new FieldError("account", "type", "deve ser CORRENTE ou POUPANCA");


        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        ProblemDetail response = exceptionHandler.handleValidationErrors(methodArgumentNotValidException);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Erro de Validação", response.getTitle());
        assertEquals(URI.create("https://techbank.com/errors/validation"), response.getType());


        String expectedDetail = "accountNumber: não pode estar vazio, type: deve ser CORRENTE ou POUPANCA";
        assertEquals(expectedDetail, response.getDetail());
    }

    @Test
    @DisplayName("Deve retornar 500 Internal Server Error para exceções genéricas")
    void handleGeneralException_ShouldReturnInternalServerError() {
        String errorMessage = "Falha de conexão com o banco de dados";
        RuntimeException exception = new RuntimeException(errorMessage);

        ProblemDetail response = exceptionHandler.handleGeneralException(exception);


        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
        assertEquals("Erro Interno do Servidor", response.getTitle());
        assertEquals(errorMessage, response.getDetail());
    }
}