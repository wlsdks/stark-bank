package com.example.cqrs.command.usecase

import com.example.cqrs.command.dto.CreateAccountRequest
import com.example.cqrs.command.dto.DepositRequest
import com.example.cqrs.command.dto.TransferRequest
import com.example.cqrs.command.dto.WithdrawRequest

interface AccountCommandUseCase {
    fun createAccount(request: CreateAccountRequest): String
    fun depositMoney(request: DepositRequest): Double
    fun withdrawMoney(request: WithdrawRequest): Double
    fun transfer(request: TransferRequest)
}