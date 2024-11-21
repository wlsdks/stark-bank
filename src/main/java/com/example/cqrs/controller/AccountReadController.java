package com.example.cqrs.controller;

import com.example.cqrs.controller.dto.AccountDto;
import com.example.cqrs.controller.dto.AccountEventDto;
import com.example.cqrs.entity.read.AccountReadEntity;
import com.example.cqrs.entity.write.event.base.BaseAccountEvent;
import com.example.cqrs.service.AccountReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/accounts")
@RequiredArgsConstructor
@RestController
public class AccountReadController {

    private final AccountReadService accountReadService;

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDto> getAccount(
            @PathVariable String accountId,
            @RequestHeader("X-User-Id") String userId) {
        AccountReadEntity account = accountReadService.getAccount(accountId);
        return ResponseEntity.ok(AccountDto.from(account));
    }

    @GetMapping("/{accountId}/history")
    public ResponseEntity<List<AccountEventDto>> getAccountHistory(
            @PathVariable String accountId,
            @RequestHeader("X-User-Id") String userId) {
        List<BaseAccountEvent> events = accountReadService.getAccountHistory(accountId);
        return ResponseEntity.ok(events.stream()
                .map(AccountEventDto::from)
                .collect(Collectors.toList()));
    }

    @GetMapping("/user/{userId}/transactions")
    public ResponseEntity<List<AccountEventDto>> getUserTransactions(
            @PathVariable String userId) {
        List<BaseAccountEvent> events = accountReadService.getUserTransactions(userId);
        return ResponseEntity.ok(events.stream()
                .map(AccountEventDto::from)
                .collect(Collectors.toList()));
    }


    @GetMapping("/transactions/{correlationId}")
    public ResponseEntity<List<AccountEventDto>> getRelatedTransactions(
            @PathVariable String correlationId,
            @RequestHeader("X-User-Id") String userId) {
        List<BaseAccountEvent> events = accountReadService.getRelatedTransactions(correlationId);
        return ResponseEntity.ok(events.stream()
                .map(AccountEventDto::from)
                .collect(Collectors.toList()));
    }

}
