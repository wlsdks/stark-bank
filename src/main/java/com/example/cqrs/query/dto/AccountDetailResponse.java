package com.example.cqrs.query.dto;

import com.example.cqrs.query.entity.AccountView;
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
    public static AccountDetailResponse from(AccountView accountView) {
        return AccountDetailResponse.builder()
                .accountId(accountView.getAccountId())
                .balance(accountView.getBalance())
                .build();
    }

}