package com.example.cqrs.application.event.retry

import com.example.cqrs.infrastructure.eventstore.base.AccountEvent
import com.example.cqrs.infrastructure.eventstore.enumerate.EventStatus
import com.example.cqrs.application.command.account.service.usecase.AccountEventStoreUseCase
import com.example.cqrs.common.exception.EventReplayException
import com.example.cqrs.application.event.handler.AccountEventListener
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class EventReplayService(
    private val accountEventStoreUseCase: AccountEventStoreUseCase,
    private val accountEventListener: AccountEventListener
) : EventReplayUseCase {

    override fun replayEvents(
        accountId: String,
        fromData: LocalDateTime
    ) {
        val events = accountEventStoreUseCase.getEvents(accountId, fromData)

        events.forEach { event ->
            if (event.status != EventStatus.PROCESSED) {
                try {
                    replayEvent(event)
                    event.markAsProcessed()
                } catch (e: Exception) {
                    event.markAsFailed()
                    throw EventReplayException("이벤트 재처리 실패: " + event.id)
                }
            }
        }
    }

    private fun replayEvent(event: AccountEvent) {
        when (event) {
            is com.example.cqrs.infrastructure.eventstore.event.account.AccountCreatedEvent -> accountEventListener.handleAccountCreated(event)
            // 추가 필요
            else -> throw IllegalArgumentException("지원하지 않는 이벤트 타입: ${event.javaClass.simpleName}")
        }
    }

}