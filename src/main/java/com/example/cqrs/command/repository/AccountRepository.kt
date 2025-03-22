package com.example.cqrs.command.repository

import com.example.cqrs.command.entity.AccountEntity
import org.springframework.data.jpa.repository.JpaRepository

interface AccountRepository : JpaRepository<AccountEntity, String> {
}