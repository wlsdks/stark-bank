package com.example.cqrs.command.controller;

import com.example.cqrs.command.dto.CreateAccountRequest;
import com.example.cqrs.command.dto.DepositRequest;
import com.example.cqrs.command.dto.TransferRequest;
import com.example.cqrs.command.dto.WithdrawRequest;
import com.example.cqrs.command.usecase.AccountCommandUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/accounts")
@RequiredArgsConstructor
@RestController
public class AccountCommandController {

    private final AccountCommandUseCase accountCommandUseCase;

    /**
     * @param accountId 계좌 ID
     * @param userId    사용자 ID
     * @return 계좌 생성 결과 메시지
     * @apiNote 계좌를 생성합니다.
     */
    @PostMapping("/{accountId}")
    public ResponseEntity<String> createAccount(
            @PathVariable String accountId,
            @RequestHeader("X-User-Id") String userId
    ) {
        CreateAccountRequest request = CreateAccountRequest.of(accountId, userId);
        accountCommandUseCase.createAccount(request);
        return ResponseEntity.ok("계좌가 생성되었습니다.");
    }

    /**
     * @param accountId 계좌 ID
     * @param amount    입금 금액
     * @param userId    사용자 ID
     * @return 입금 결과 메시지
     * @apiNote 계좌에 입금을 수행합니다.
     */
    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<String> deposit(
            @PathVariable String accountId,
            @RequestParam double amount,
            @RequestHeader("X-User-Id") String userId
    ) {
        DepositRequest request = DepositRequest.of(accountId, amount, userId);
        accountCommandUseCase.depositMoney(request);
        return ResponseEntity.ok("입금이 완료되었습니다.");
    }

    /**
     * @param accountId 계좌 ID
     * @param amount    출금 금액
     * @param userId    사용자 ID
     * @return 출금 결과 메시지
     * @apiNote 계좌에서 출금을 수행합니다.
     */
    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<String> withdraw(
            @PathVariable String accountId,
            @RequestParam double amount,
            @RequestHeader("X-User-Id") String userId
    ) {
        WithdrawRequest request = WithdrawRequest.of(accountId, amount, userId);
        accountCommandUseCase.withdrawMoney(request);
        return ResponseEntity.ok("출금이 완료되었습니다.");
    }

    /**
     * @param request 이체 요청 DTO
     * @param userId  요청한 사용자 ID
     * @return 이체 결과 메시지
     * @apiNote 계좌 간 이체를 수행합니다.
     */
    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(
            @RequestBody TransferRequest request,
            @RequestHeader("X-User-Id") String userId
    ) {
        request = TransferRequest.of(
                request.getFromAccountId(),
                request.getToAccountId(),
                request.getAmount(),
                userId
        );

        // 이체 요청을 처리합니다.
        accountCommandUseCase.transfer(request);
        return ResponseEntity.ok("이체가 완료되었습니다.");
    }

}