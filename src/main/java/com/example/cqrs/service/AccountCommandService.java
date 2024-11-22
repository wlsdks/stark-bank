package com.example.cqrs.service;

public interface AccountCommandService {

    void createAccount(String accountId, String userId);

    void depositMoney(String accountId, double amount, String userId);

    void withdrawMoney(String accountId, double amount, String userId);

}
