package com.example.cqrs.command.dto;

import lombok.*;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class TransferRequest {

    private String fromAccountId;
    private String toAccountId;
    private double amount;
    private String userId;

    // factory method
    public static TransferRequest of(String fromAccountId, String toAccountId, double amount, String userId) {
        return new TransferRequest(fromAccountId, toAccountId, amount, userId);
    }

}
