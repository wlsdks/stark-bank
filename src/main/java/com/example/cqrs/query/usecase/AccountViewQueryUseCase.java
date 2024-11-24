package com.example.cqrs.query.usecase;

import com.example.cqrs.query.entity.AccountView;

import java.util.Optional;

public interface AccountViewQueryUseCase {

    Optional<AccountView> getAccount(String accountId);

}
