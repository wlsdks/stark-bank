package com.example.cqrs.controller;

import com.example.cqrs.controller.dto.AccountDto;
import com.example.cqrs.controller.dto.AccountEventDto;
import com.example.cqrs.entity.read.AccountView;
import com.example.cqrs.entity.write.event.base.AbstractAccountEvent;
import com.example.cqrs.service.AccountQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/accounts")
@RequiredArgsConstructor
@RestController
public class AccountQueryController {

    private final AccountQueryService accountQueryService;

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDto> getAccount(
            @PathVariable String accountId,
            @RequestHeader("X-User-Id") String userId) {
        AccountView account = accountQueryService.getAccount(accountId);
        return ResponseEntity.ok(AccountDto.from(account));
    }

    @GetMapping("/{accountId}/history")
    public ResponseEntity<List<AccountEventDto>> getAccountHistory(
            @PathVariable String accountId,
            @RequestHeader("X-User-Id") String userId) {
        List<AbstractAccountEvent> events = accountQueryService.getAccountHistory(accountId);
        return ResponseEntity.ok(events.stream()
                .map(AccountEventDto::from)
                .collect(Collectors.toList()));
    }

    @GetMapping("/user/{userId}/transactions")
    public ResponseEntity<List<AccountEventDto>> getUserTransactions(
            @PathVariable String userId) {
        List<AbstractAccountEvent> events = accountQueryService.getUserTransactions(userId);
        return ResponseEntity.ok(events.stream()
                .map(AccountEventDto::from)
                .collect(Collectors.toList()));
    }


    @GetMapping("/transactions/{correlationId}")
    public ResponseEntity<List<AccountEventDto>> getRelatedTransactions(
            @PathVariable String correlationId,
            @RequestHeader("X-User-Id") String userId) {
        List<AbstractAccountEvent> events = accountQueryService.getRelatedTransactions(correlationId);
        return ResponseEntity.ok(events.stream()
                .map(AccountEventDto::from)
                .collect(Collectors.toList()));
    }

}
