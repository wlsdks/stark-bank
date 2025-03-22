package com.example.cqrs.common.schedule

import com.example.cqrs.command.entity.event.AbstractAccountEventEntity
import com.example.cqrs.command.entity.event.enumerate.EventStatus
import com.example.cqrs.command.usecase.AccountEventStoreUseCase
import com.example.cqrs.command.usecase.AccountUseCase
import com.example.cqrs.common.service.EventReplayService
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
    ): List<AbstractAccountEventEntity> {
        return accountEventStoreUseCase.getEvents(accountId, fromDate)
            .filter { event -> event.status != EventStatus.PROCESSED }
    }

}