package com.example.cqrs.query.controller;

import com.example.cqrs.common.dto.AccountDto;
import com.example.cqrs.common.dto.AccountEventDto;
import com.example.cqrs.query.entity.AccountView;
import com.example.cqrs.command.entity.event.AbstractAccountEvent;
import com.example.cqrs.query.usecase.AccountQueryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/accounts")
@RequiredArgsConstructor
@RestController
public class AccountQueryController {

    private final AccountQueryUseCase accountQueryUseCase;

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDto> getAccount(
            @PathVariable String accountId,
            @RequestHeader("X-User-Id") String userId) {
        AccountView account = accountQueryUseCase.getAccount(accountId);
        return ResponseEntity.ok(AccountDto.from(account));
    }

    @GetMapping("/{accountId}/history")
    public ResponseEntity<List<AccountEventDto>> getAccountHistory(
            @PathVariable String accountId,
            @RequestHeader("X-User-Id") String userId) {
        List<AbstractAccountEvent> events = accountQueryUseCase.getAccountHistory(accountId);
        return ResponseEntity.ok(events.stream()
                .map(AccountEventDto::from)
                .collect(Collectors.toList()));
    }

    @GetMapping("/user/{userId}/transactions")
    public ResponseEntity<List<AccountEventDto>> getUserTransactions(
            @PathVariable String userId) {
        List<AbstractAccountEvent> events = accountQueryUseCase.getUserTransactions(userId);
        return ResponseEntity.ok(events.stream()
                .map(AccountEventDto::from)
                .collect(Collectors.toList()));
    }


    @GetMapping("/transactions/{correlationId}")
    public ResponseEntity<List<AccountEventDto>> getRelatedTransactions(
            @PathVariable String correlationId,
            @RequestHeader("X-User-Id") String userId) {
        List<AbstractAccountEvent> events = accountQueryUseCase.getRelatedTransactions(correlationId);
        return ResponseEntity.ok(events.stream()
                .map(AccountEventDto::from)
                .collect(Collectors.toList()));
    }

}
