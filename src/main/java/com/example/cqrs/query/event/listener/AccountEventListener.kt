package com.example.cqrs.query.event.listener

import com.example.cqrs.command.entity.event.account.AccountCreatedEvent
import com.example.cqrs.command.entity.event.base.Event
import com.example.cqrs.command.entity.event.money.*
import com.example.cqrs.query.document.AccountDocument
import com.example.cqrs.query.repository.AccountQueryMongoRepository
import org.slf4j.LoggerFactory
import org.springframework.retry.support.RetryTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

/**
 * MongoDB 읽기 모델을 업데이트하는 이벤트 핸들러입니다.
 * 계좌 관련 이벤트를 처리하여 MongoDB에 저장된 읽기 모델(AccountDocument)을 업데이트합니다.
 */
@Service
class AccountEventListener(
    private val accountQueryMongoRepository: AccountQueryMongoRepository,
    private val retryTemplate: RetryTemplate
) {
    private val log = LoggerFactory.getLogger(AccountEventListener::class.java)

    /**
     * 계좌 생성 이벤트 처리
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleAccountCreated(event: AccountCreatedEvent) {
        log.info("계좌 생성 이벤트 처리: {}", event.accountId)
        retryTemplate.execute<Void, Exception> { _ ->
            val accountDocument = AccountDocument.of(
                accountId = event.accountId,
                balance = event.amount ?: 0.0,
                lastUpdated = event.eventDate
            )
            accountQueryMongoRepository.save(accountDocument)
            log.info("계좌 생성 완료: {}", event.accountId)
            null
        }
    }

    /**
     * 입금 이벤트 처리
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleMoneyDeposited(event: MoneyDepositedEvent) {
        log.info("입금 이벤트 처리: {}", event.accountId)
        updateBalance(event.accountId, event.amount ?: 0.0, true, event)
    }


    /**
     * 출금 이벤트 처리
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleMoneyWithdrawn(event: MoneyWithdrawnEvent) {
        log.info("출금 이벤트 처리: {}", event.accountId)
        updateBalance(event.accountId, event.amount ?: 0.0, false, event)
    }


    /**
     * 이체 출금 이벤트 처리
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleMoneyTransferredOut(event: MoneyTransferredOutEvent) {
        log.info("이체 출금 이벤트 처리: {} -> {}", event.accountId, event.targetAccountId)
        updateBalance(event.accountId, event.amount ?: 0.0, false, event)
    }


    /**
     * 이체 입금 이벤트 처리
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleMoneyTransferredIn(event: MoneyTransferredInEvent) {
        log.info("이체 입금 이벤트 처리: {} <- {}", event.accountId, event.sourceAccountId)
        updateBalance(event.accountId, event.amount ?: 0.0, true, event)
    }


    /**
     * 잔액 변경 실패 이벤트 처리 (감사 로그 저장 등)
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleBalanceChangeFailed(event: BalanceChangeFailedEvent) {
        log.warn("잔액 변경 실패: {}, 사유: {}", event.accountId, event.reason)
        // 실패 로그 또는 알림 처리 로직
    }

    /**
     * 계좌 잔액 업데이트 공통 메서드
     */
    private fun updateBalance(
        accountId: String,
        amount: Double,
        isDeposit: Boolean,
        event: Event
    ) {
        try {
            retryTemplate.execute<Void, Exception> { _ ->
                val accountDocument = accountQueryMongoRepository.findByAccountId(accountId)
                    ?: throw IllegalStateException("계좌를 찾을 수 없음: $accountId")

                val newBalance = if (isDeposit) {
                    accountDocument.balance + amount
                } else {
                    accountDocument.balance - amount
                }

                accountDocument.changeBalance(newBalance)
                accountDocument.lastUpdated = event.eventDate
                accountQueryMongoRepository.save(accountDocument)
                null
            }
        } catch (e: Exception) {
            log.error("잔액 업데이트 실패: {}", accountId, e)
            // 이벤트 처리 실패 시 상태 업데이트 (별도 복구 프로세스용)
            markEventAsFailed(event)
        }
    }

    /**
     * 이벤트 처리 실패 시 상태 업데이트
     */
    private fun markEventAsFailed(event: Event) {
        event.markAsFailed()
        // 이벤트 저장 로직 (별도 저장소 또는 DB 업데이트)
    }

}