package com.example.cqrs.query.service;

import com.example.cqrs.query.document.AccountDocument;
import com.example.cqrs.query.repository.AccountViewRepository;
import com.example.cqrs.query.usecase.AccountViewQueryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AccountViewQueryService implements AccountViewQueryUseCase {

    private final AccountViewRepository accountViewRepository;

    public Optional<AccountDocument> getAccount(String accountId) {
        return accountViewRepository.findById(accountId);
    }

}
