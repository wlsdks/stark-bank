package com.example.cqrs.interfaces.scheduler.eventreplay

import com.example.cqrs.infrastructure.eventstore.base.AccountEvent
import com.example.cqrs.infrastructure.eventstore.enumerate.EventStatus
import com.example.cqrs.application.command.account.service.usecase.AccountEventStoreUseCase
import com.example.cqrs.application.command.account.service.usecase.AccountUseCase
import com.example.cqrs.application.event.retry.EventReplayService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class EventReplayScheduler(
    private val eventReplayService: EventReplayService,
    private val accountUseCase: AccountUseCase,
    private val accountEventStoreUseCase: AccountEventStoreUseCase
) {

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정
    fun syncDailyEvents() {
        val yesterday = LocalDateTime.now().minusDays(1)

        try {
            val activeAccountIds = accountUseCase.getActiveAccountIds()

            activeAccountIds.forEach { accountId ->
                val findUnprocessedEvents = findUnprocessedEvents(accountId, yesterday)

                if (findUnprocessedEvents.isNotEmpty()) {
                    eventReplayService.replayEvents(accountId, yesterday)
                }
            }
        } catch (e: Exception) {
            // 예외 처리
        }
    }

    /**
     * 지정된 날짜 이후에 처리되지 않은 이벤트를 조회합니다.
     *
     * @param accountId 계좌 ID
     * @param fromDate  조회 시작 날짜
     * @return 처리되지 않은 이벤트 목록
     */
    private fun findUnprocessedEvents(
        accountId: String,
        fromDate: LocalDateTime
    ): List<AccountEvent> {
        return accountEventStoreUseCase.getEvents(accountId, fromDate)
            .filter { event -> event.status != EventStatus.PROCESSED }
    }

}