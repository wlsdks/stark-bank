package com.example.cqrs.common.dto;

import com.example.cqrs.query.entity.AccountView;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AccountDto {

    private String accountId;
    private double balance;
    
    public static AccountDto from(AccountView entity) {
        return new AccountDto(entity.getAccountId(), entity.getBalance());
    }

}