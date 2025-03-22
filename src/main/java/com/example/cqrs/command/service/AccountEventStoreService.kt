package com.example.cqrs.command.service

import com.example.cqrs.command.entity.event.base.AccountEvent
import com.example.cqrs.command.entity.event.base.Event
import com.example.cqrs.command.repository.AccountEventRepository
import com.example.cqrs.command.usecase.AccountEventStoreUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional
@Service
class AccountEventStoreService(
    private val accountEventRepository: AccountEventRepository
) : AccountEventStoreUseCase {

    /**
     * 새로운 이벤트를 저장합니다.
     * 저장 전 이벤트의 유효성을 검증합니다.
     *
     * @param event 이벤트
     */
    override fun save(event: Event) {
        if (event is AccountEvent) {
            validateEvent(event)
            accountEventRepository.save(event)
        } else {
            throw IllegalArgumentException("지원하지 않는 이벤트 타입입니다: ${event.javaClass.name}")
        }
    }

    /**
     * 이벤트 처리 상태를 저장합니다.
     *
     * @param event 이벤트
     */
    override fun saveEventStatus(event: Event) {
        if (event is AccountEvent) {
            accountEventRepository.save(event)
        } else {
            throw IllegalArgumentException("지원하지 않는 이벤트 타입입니다: ${event.javaClass.name}")
        }
    }

    /**
     * 계좌 ID로 이벤트를 조회합니다.
     * 특정 날짜 이후의 계좌 이벤트를 조회합니다.
     *
     * @param accountId 계좌 ID
     * @param after 조회 시작 일시
     * @return 이벤트 목록
     */
    override fun getEvents(
        accountId: String,
        after: LocalDateTime
    ): List<AccountEvent> {
        return accountEventRepository.findByAccountIdAndEventDateAfterOrderByEventDateAsc(accountId, after)
    }

    /**
     * 계좌 ID로 모든 이벤트를 조회합니다.
     *
     * @param accountId 계좌 ID
     * @return 이벤트 목록
     */
    override fun getAllEvents(
        accountId: String
    ): List<AccountEvent> {
        return accountEventRepository.findByAccountIdOrderByEventDateAsc(accountId)
    }

    /**
     * 메타데이터의 연관 ID로 이벤트를 조회합니다.
     *
     * @param correlationId 상관 ID
     * @return 이벤트 목록
     */
    override fun findByMetadataCorrelationId(
        correlationId: String
    ): List<AccountEvent> {
        return accountEventRepository.findByMetadataCorrelationId(correlationId)
    }

    /**
     * 메타데이터의 사용자 ID로 이벤트를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 이벤트 목록
     */
    override fun findByMetadataUserId(
        userId: String
    ): List<AccountEvent> {
        return accountEventRepository.findByMetadataUserIdOrderByEventDateDesc(userId)
    }

    /**
     * 계좌 ID로 이벤트 건수를 조회합니다.
     * 특정 날짜 이후의 이벤트 건수를 조회할 때 사용됩니다.
     *
     * @param accountId 계좌 ID
     * @param afterDate 조회 시작 일시
     * @return 이벤트 건수
     */
    override fun countEventsAfterDate(
        accountId: String,
        afterDate: LocalDateTime
    ): Long {
        return accountEventRepository.countByAccountIdAndEventDateAfter(accountId, afterDate)
    }

    /**
     * 이벤트 시간 유효성 검증
     * 이전 이벤트보다 이벤트 발생 시간이 빠른 경우 예외 발생
     *
     * @param event 검증할 이벤트
     */
    private fun validateEvent(event: AccountEvent) {
        val existingEvents = accountEventRepository.findByAccountIdOrderByEventDateDesc(event.accountId)

        if (existingEvents.isNotEmpty()) {
            val lastEvent = existingEvents.first()
            if (lastEvent.eventDate.isAfter(event.eventDate)) {
                throw IllegalArgumentException(
                    "이벤트 발생 일시가 이전 이벤트보다 빠릅니다." +
                            " 마지막 이벤트 시간: ${lastEvent.eventDate}, 현재 이벤트 시간: ${event.eventDate}"
                )
            }
        }
    }

}