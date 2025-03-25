package com.example.cqrs.application.account.event.retry

import com.example.cqrs.application.account.command.service.usecase.AccountEventStoreUseCase
import com.example.cqrs.application.account.event.handler.EventDispatcher
import com.example.cqrs.common.exception.EventReplayException
import com.example.cqrs.infrastructure.eventstore.entity.enumerate.EventStatus
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class EventReplayService(
    private val accountEventStoreUseCase: AccountEventStoreUseCase,
    private val eventDispatcher: EventDispatcher
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
                    eventDispatcher.dispatch(event)
                    event.markAsProcessed()
                    accountEventStoreUseCase.saveEventStatus(event)
                } catch (e: Exception) {
                    event.markAsFailed()
                    accountEventStoreUseCase.saveEventStatus(event)
                    throw EventReplayException("이벤트 재처리 실패: " + event.id)
                }
            }
        }
    }

}