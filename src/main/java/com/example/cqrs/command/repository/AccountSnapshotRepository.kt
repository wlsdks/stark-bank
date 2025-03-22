package com.example.cqrs.command.repository

import com.example.cqrs.command.entity.AccountSnapshotEntity
import org.springframework.data.jpa.repository.JpaRepository

interface AccountSnapshotRepository : JpaRepository<AccountSnapshotEntity, String> {
}