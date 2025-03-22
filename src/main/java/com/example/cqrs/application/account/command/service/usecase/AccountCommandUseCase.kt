package com.example.cqrs.application.account.command.service.usecase

import com.example.cqrs.interfaces.api.command.account.dto.request.CreateAccountRequest
import com.example.cqrs.interfaces.api.command.account.dto.request.DepositRequest
import com.example.cqrs.interfaces.api.command.account.dto.request.TransferRequest
import com.example.cqrs.interfaces.api.command.account.dto.request.WithdrawRequest

interface AccountCommandUseCase {
    fun createAccount(request: CreateAccountRequest): String
    fun depositMoney(request: DepositRequest): Double
    fun withdrawMoney(request: WithdrawRequest): Double
    fun transfer(request: TransferRequest)
}