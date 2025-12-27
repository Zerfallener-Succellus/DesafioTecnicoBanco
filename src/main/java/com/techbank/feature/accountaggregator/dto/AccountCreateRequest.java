package com.techbank.feature.accountaggregator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AccountCreateRequest(
        @NotBlank(message = "O num da conta é obrigatório")
        String accountNumber,

        @NotBlank(message = "O nome do titular é obrigatório")
        String holderName,

        @Pattern(regexp = "CORRENTE|POUPANCA", message = "deve ser CORRENTE ou POUPANCA")
        String type
) {}