package com.example.cqrs.command.controller;

import com.example.cqrs.command.dto.read.AccountDetailResponse;
import com.example.cqrs.command.dto.read.AccountTransactionResponse;
import com.example.cqrs.command.usecase.AccountUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 계좌 조회 관련 API를 제공하는 컨트롤러입니다.
 * CQRS 패턴의 Query(조회) 부분을 담당합니다.
 */
@RequestMapping("/accounts")
@RequiredArgsConstructor
@RestController
public class AccountController {

    private final AccountUseCase accountUseCase;

    /**
     * 특정 계좌의 상세 정보를 조회합니다.
     *
     * @param accountId 조회할 계좌 ID
     * @param userId    요청한 사용자 ID
     * @return 계좌 상세 정보
     */
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDetailResponse> getAccount(
            @PathVariable String accountId,
            @RequestHeader("X-User-Id") String userId
    ) {
        AccountDetailResponse response = AccountDetailResponse.from(accountUseCase.getAccount(accountId));
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 계좌의 거래 이력을 조회합니다.
     *
     * @param accountId 조회할 계좌 ID
     * @param userId    요청한 사용자 ID
     * @return 계좌의 모든 거래 이력
     */
    @GetMapping("/{accountId}/history")
    public ResponseEntity<List<AccountTransactionResponse>> getAccountHistory(
            @PathVariable String accountId,
            @RequestHeader("X-User-Id") String userId
    ) {
        List<AccountTransactionResponse> response = accountUseCase.getAccountHistory(accountId).stream()
                .map(AccountTransactionResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * 특정 사용자의 모든 거래 이력을 조회합니다.
     *
     * @param userId 조회할 사용자 ID
     * @return 사용자의 모든 거래 이력
     */
    @GetMapping("/user/{userId}/transactions")
    public ResponseEntity<List<AccountTransactionResponse>> getUserTransactions(
            @PathVariable String userId
    ) {
        List<AccountTransactionResponse> response = accountUseCase.getUserTransactions(userId).stream()
                .map(AccountTransactionResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * 연관된 거래들을 correlationId를 기준으로 조회합니다.
     *
     * @param correlationId 조회할 연관 ID
     * @param userId        요청한 사용자 ID
     * @return 연관된 모든 거래 이력
     */
    @GetMapping("/transactions/{correlationId}")
    public ResponseEntity<List<AccountTransactionResponse>> getRelatedTransactions(
            @PathVariable String correlationId,
            @RequestHeader("X-User-Id") String userId
    ) {
        List<AccountTransactionResponse> response = accountUseCase.getRelatedTransactions(correlationId).stream()
                .map(AccountTransactionResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

}
