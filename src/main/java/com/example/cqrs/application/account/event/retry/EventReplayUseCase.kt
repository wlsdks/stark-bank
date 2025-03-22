package com.example.cqrs.application.account.event.retry

import java.time.LocalDateTime

interface EventReplayUseCase {
    fun replayEvents(accountId: String, fromData: LocalDateTime)
}