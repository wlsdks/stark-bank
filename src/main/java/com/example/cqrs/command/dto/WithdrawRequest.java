package com.example.cqrs.command.dto;

import lombok.*;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class WithdrawRequest {

    private String accountId;
    private double amount;
    private String userId;

    // factory method
    public static WithdrawRequest of(String accountId, double amount, String userId) {
        return new WithdrawRequest(accountId, amount, userId);
    }

}