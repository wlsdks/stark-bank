package com.example.cqrs.controller.dto;

import com.example.cqrs.entity.read.AccountReadEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AccountDto {

    private String accountId;
    private double balance;
    
    public static AccountDto from(AccountReadEntity entity) {
        return new AccountDto(entity.getAccountId(), entity.getBalance());
    }

}