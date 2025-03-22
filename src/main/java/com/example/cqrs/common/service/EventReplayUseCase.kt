package com.example.cqrs.common.service

import java.time.LocalDateTime

interface EventReplayUseCase {
    fun replayEvents(accountId: String, fromData: LocalDateTime)
}