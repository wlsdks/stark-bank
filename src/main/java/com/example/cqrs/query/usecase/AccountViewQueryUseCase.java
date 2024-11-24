package com.example.cqrs.query.usecase;

import com.example.cqrs.query.document.AccountDocument;

import java.util.Optional;

public interface AccountViewQueryUseCase {

    Optional<AccountDocument> getAccount(String accountId);

}
