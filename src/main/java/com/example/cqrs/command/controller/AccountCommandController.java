package com.example.cqrs.command.controller;

import com.example.cqrs.command.dto.CreateAccountRequest;
import com.example.cqrs.command.dto.DepositRequest;
import com.example.cqrs.command.dto.WithdrawRequest;
import com.example.cqrs.command.usecase.AccountCommandUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/accounts")
@RequiredArgsConstructor
@RestController
public class AccountCommandController {

    private final AccountCommandUseCase accountWriteService;

    @PostMapping("/{accountId}")
    public ResponseEntity<String> createAccount(
            @PathVariable String accountId,
            @RequestHeader("X-User-Id") String userId
    ) {
        CreateAccountRequest request = new CreateAccountRequest(accountId, userId);
        accountWriteService.createAccount(request);
        return ResponseEntity.ok("계좌가 생성되었습니다.");
    }

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<String> deposit(
            @PathVariable String accountId,
            @RequestParam double amount,
            @RequestHeader("X-User-Id") String userId
    ) {
        DepositRequest request = new DepositRequest(accountId, amount, userId);
        accountWriteService.depositMoney(request);
        return ResponseEntity.ok("입금이 완료되었습니다.");
    }

    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<String> withdraw(
            @PathVariable String accountId,
            @RequestParam double amount,
            @RequestHeader("X-User-Id") String userId
    ) {
        WithdrawRequest request = new WithdrawRequest(accountId, amount, userId);
        accountWriteService.withdrawMoney(request);
        return ResponseEntity.ok("출금이 완료되었습니다.");
    }

}