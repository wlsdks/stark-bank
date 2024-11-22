package com.example.cqrs.command.dto;

import lombok.*;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class DepositRequest {

    private String accountId;
    private double amount;
    private String userId;

    // factory method
    public static DepositRequest of(String accountId, double amount, String userId) {
        return new DepositRequest(accountId, amount, userId);
    }

}