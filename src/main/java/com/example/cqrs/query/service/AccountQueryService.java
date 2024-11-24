package com.example.cqrs.query.service;

import com.example.cqrs.query.document.AccountDocument;
import com.example.cqrs.query.repository.AccountQueryRepository;
import com.example.cqrs.query.usecase.AccountQueryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AccountQueryService implements AccountQueryUseCase {

    private final AccountQueryRepository accountQueryRepository;

    public Optional<AccountDocument> getAccount(String accountId) {
        return accountQueryRepository.findById(accountId);
    }

}
