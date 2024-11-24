package com.example.cqrs.command.dto.read;

import com.example.cqrs.command.entity.AccountEntity;
import lombok.*;

/**
 * 계좌 상세 정보에 대한 Response DTO입니다.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class AccountDetailResponse {

    private String accountId;
    private double balance;

    // factory method
    public static AccountDetailResponse from(AccountEntity accountEntity) {
        return AccountDetailResponse.builder()
                .accountId(accountEntity.getAccountId())
                .balance(accountEntity.getBalance())
                .build();
    }

}