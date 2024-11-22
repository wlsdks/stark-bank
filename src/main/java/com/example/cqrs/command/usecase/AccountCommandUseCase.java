package com.example.cqrs.command.usecase;

import com.example.cqrs.command.dto.CreateAccountRequest;
import com.example.cqrs.command.dto.DepositRequest;
import com.example.cqrs.command.dto.TransferRequest;
import com.example.cqrs.command.dto.WithdrawRequest;

public interface AccountCommandUseCase {

    void createAccount(CreateAccountRequest request);

    void depositMoney(DepositRequest request);

    void withdrawMoney(WithdrawRequest request);

    void transfer(TransferRequest request);

}
