package com.example.cqrs.query.usecase;

import com.example.cqrs.query.document.AccountView;

import java.util.Optional;

public interface AccountViewQueryUseCase {

    Optional<AccountView> getAccount(String accountId);

}
