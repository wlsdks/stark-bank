package com.example.cqrs.application.account.event.retry

import com.example.cqrs.application.account.command.service.usecase.AccountEventStoreUseCase
import com.example.cqrs.application.account.event.handler.AccountEventListener
import com.example.cqrs.common.exception.EventReplayException
import com.example.cqrs.infrastructure.eventstore.entity.base.AccountEventBaseEntity
import com.example.cqrs.infrastructure.eventstore.entity.enumerate.EventStatus
import com.example.cqrs.infrastructure.eventstore.entity.event.account.AccountCreatedEventEntity
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class EventReplayService(
    private val accountEventStoreUseCase: AccountEventStoreUseCase,
    private val accountEventListener: AccountEventListener
) : EventReplayUseCase {

    @Scheduled(fixedRate = 300000) // 5분마다
    fun retryFailedEvents() {
        // 구현
    }

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

    private fun replayEvent(event: AccountEventBaseEntity) {
        when (event) {
            is AccountCreatedEventEntity -> accountEventListener.handleEvent(event)
            // 추가 필요
            else -> throw IllegalArgumentException("지원하지 않는 이벤트 타입: ${event.javaClass.simpleName}")
        }
    }

}