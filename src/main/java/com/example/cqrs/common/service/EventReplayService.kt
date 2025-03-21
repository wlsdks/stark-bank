package com.example.cqrs.common.service

import com.example.cqrs.command.entity.event.AbstractAccountEventEntity
import com.example.cqrs.command.entity.event.enumerate.EventStatus
import com.example.cqrs.command.usecase.AccountEventStoreUseCase
import com.example.cqrs.common.exception.EventReplayException
import com.example.cqrs.query.event.listener.AccountEventListener
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

    private fun replayEvent(event: AbstractAccountEventEntity) {
        when (event.javaClass.simpleName) {
            "AccountCreatedEventEntity" -> accountEventListener.handleAccountCreate(event)
            "AccountCreditedEventEntity" -> accountEventListener.handleDeposit(event)
            "AccountDebitedEventEntity" -> accountEventListener.handleWithdraw(event)
        }
    }

}