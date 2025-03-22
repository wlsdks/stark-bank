package com.example.cqrs.application.event.retry

import java.time.LocalDateTime

interface EventReplayUseCase {
    fun replayEvents(accountId: String, fromData: LocalDateTime)
}