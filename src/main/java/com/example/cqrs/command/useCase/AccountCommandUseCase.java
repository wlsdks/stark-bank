package com.example.cqrs.command.useCase;

public interface AccountCommandUseCase {

    void createAccount(String accountId, String userId);

    void depositMoney(String accountId, double amount, String userId);

    void withdrawMoney(String accountId, double amount, String userId);

}
