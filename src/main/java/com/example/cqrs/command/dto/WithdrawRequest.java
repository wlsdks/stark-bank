package com.example.cqrs.command.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class WithdrawRequest {

    private String accountId;
    private double amount;
    private String userId;

}