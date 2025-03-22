package com.example.cqrs.application.account.command.service.usecase

import com.example.cqrs.application.account.command.dto.request.CreateAccountRequest
import com.example.cqrs.application.account.command.dto.request.DepositRequest
import com.example.cqrs.application.account.command.dto.request.TransferRequest
import com.example.cqrs.application.account.command.dto.request.WithdrawRequest

interface AccountCommandUseCase {
    fun createAccount(request: CreateAccountRequest): String
    fun depositMoney(request: DepositRequest): Double
    fun withdrawMoney(request: WithdrawRequest): Double
    fun transfer(request: TransferRequest)
}