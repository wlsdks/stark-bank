package com.example.cqrs.controller;

import com.example.cqrs.service.AccountWriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/accounts")
@RequiredArgsConstructor
@RestController
public class AccountWriteController {

    private final AccountWriteService accountWriteService;

    @PostMapping("/{accountId}")
    public ResponseEntity<String> createAccount(
            @PathVariable String accountId,
            @RequestHeader("X-User-Id") String userId
    ) {
        accountWriteService.createAccount(accountId, userId);
        return ResponseEntity.ok("계좌가 생성되었습니다.");
    }


    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<String> deposit(
            @PathVariable String accountId,
            @RequestParam double amount,
            @RequestHeader("X-User-Id") String userId
    ) {
        accountWriteService.depositMoney(accountId, amount, userId);
        return ResponseEntity.ok("입금이 완료되었습니다.");
    }


    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<String> withdraw(
            @PathVariable String accountId,
            @RequestParam double amount,
            @RequestHeader("X-User-Id") String userId
    ) {
        accountWriteService.withdrawMoney(accountId, amount, userId);
        return ResponseEntity.ok("출금이 완료되었습니다.");
    }

}