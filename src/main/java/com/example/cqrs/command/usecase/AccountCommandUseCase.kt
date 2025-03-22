package com.example.cqrs.command.usecase

import com.example.cqrs.command.dto.CreateAccountRequest
import com.example.cqrs.command.dto.DepositRequest
import com.example.cqrs.command.dto.TransferRequest
import com.example.cqrs.command.dto.WithdrawRequest

interface AccountCommandUseCase {
    fun createAccount(createAccountRequest: CreateAccountRequest)
    fun depositMoney(depositRequest: DepositRequest)
    fun withdrawMoney(withdrawRequest: WithdrawRequest)
    fun transfer(transferRequest: TransferRequest)
}