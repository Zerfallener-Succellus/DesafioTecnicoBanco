package com.techbank.feature.accountaggregator;

import com.techbank.feature.accountaggregator.dto.AccountCreateRequest;
import com.techbank.feature.accountaggregator.dto.AccountSummaryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountAggregatorController {

    private final AccountAggregatorService service;

    @GetMapping
    public ResponseEntity<List<AccountSummaryResponse>> listAccounts() {
        return ResponseEntity.ok(service.listAllAccounts());
    }

    @PostMapping
    public ResponseEntity<AccountSummaryResponse> createAccount(@RequestBody @Valid AccountCreateRequest request) {
        AccountSummaryResponse created = service.createAccount(request);
        return ResponseEntity
                .created(URI.create("/accounts/" + created.accountId()))
                .body(created);
    }
}