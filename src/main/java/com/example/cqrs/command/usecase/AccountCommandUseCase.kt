package com.example.cqrs.command.usecase

interface AccountCommandUseCase {
    fun createAccount(createAccountRequest: CreateAccountRequest)
    fun depositMoney(depositRequest: DepositRequest)
    fun withdrawMoney(withdrawRequest: WithdrawRequest)
    fun transfer(transferRequest: TransferRequest)
}