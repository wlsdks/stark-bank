package com.example.cqrs.command.service

import com.example.cqrs.command.entity.event.AbstractAccountEventEntity
import com.example.cqrs.command.repository.AccountEventStoreRepository
import com.example.cqrs.command.usecase.AccountEventStoreUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional
@Service
class AccountEventStoreService(
    private val accountEventStoreRepository: AccountEventStoreRepository
) : AccountEventStoreUseCase {

    /**
     * 새로운 이벤트를 저장합니다.
     * 저장 전 이벤트의 유효성을 검증합니다.
     *
     * @param event 이벤트
     */
    override fun save(
        event: AbstractAccountEventEntity
    ) {
        validateEvent(event)
        accountEventStoreRepository.save(event)
    }

    /**
     * 이벤트 처리 상태를 저장합니다.
     *
     * @param event 이벤트
     */
    override fun saveEventStatus(
        event: AbstractAccountEventEntity
    ) {
        accountEventStoreRepository.save(event)
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
    ): List<AbstractAccountEventEntity> {
        return accountEventStoreRepository.findByAccountIdAndEventDateAfterOrderByEventDateAsc(accountId, after)
    }

    /**
     * 계좌 ID로 모든 이벤트를 조회합니다.
     *
     * @param accountId 계좌 ID
     * @return 이벤트 목록
     */
    override fun getAllEvents(
        accountId: String
    ): List<AbstractAccountEventEntity> {
        return accountEventStoreRepository.findByAccountIdOrderByEventDateAsc(accountId)
    }

    /**
     * 메타데이터의 연관 ID로 이벤트를 조회합니다.
     *
     * @param correlationId 상관 ID
     * @return 이벤트 목록
     */
    override fun findByMetadataCorrelationId(
        correlationId: String
    ): List<AbstractAccountEventEntity> {
        return accountEventStoreRepository.findByMetadataCorrelationId(correlationId)
    }

    /**
     * 메타데이터의 사용자 ID로 이벤트를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 이벤트 목록
     */
    override fun findByMetadataUserId(
        userId: String
    ): List<AbstractAccountEventEntity> {
        return accountEventStoreRepository.findByMetadataUserIdOrderByEventDateDesc(userId)
    }

    /**
     * 계좌 ID로 이벤트 건수를 조회합니다.
     * 특정 사용자의 모든 거래 이력을 조회할 때 사용됩니다.
     *
     * @param accountId 계좌 ID
     * @return 이벤트 건수
     */
    override fun countEventsAfterDate(
        accountId: String,
        afterDate: LocalDateTime
    ): Long {
        return accountEventStoreRepository.countByAccountIdAndEventDateAfter(accountId, afterDate)
    }

    /**
     * 이벤트 저장
     *
     * @param event 이벤트
     */
    private fun validateEvent(
        event: AbstractAccountEventEntity
    ) {
        val existingEvents = accountEventStoreRepository.findByAccountIdOrderByEventDateDesc(event.accountId)

        if (existingEvents.isNotEmpty()) {
            val lastEvent = existingEvents.first()
            if (lastEvent.eventDate.isAfter(event.eventDate)) {
                throw IllegalArgumentException("이벤트 발생 일시가 이전 이벤트보다 빠릅니다.")
            }
        }
    }

}