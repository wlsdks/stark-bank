package com.example.cqrs.query.event.listener

import com.example.cqrs.command.entity.event.AbstractAccountEventEntity
import com.example.cqrs.command.entity.event.AccountCreatedEventEntity
import com.example.cqrs.command.entity.event.MoneyDepositedEventEntity
import com.example.cqrs.command.entity.event.MoneyWithdrawnEventEntity
import com.example.cqrs.common.exception.EventHandlingException
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
     * 계좌 생성 이벤트를 처리합니다.
     * MongoDB에 새로운 읽기 모델 계정을 생성합니다.
     *
     * @param event 계좌 생성 이벤트
     * @throws EventHandlingException 모든 재시도 후에도 처리 실패 시
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleAccountCreate(event: AccountCreatedEventEntity) {
        log.info("계좌 생성 이벤트 처리 시작: {}", event.accountId)
        try {
            retryTemplate.execute<Void, Exception> { _ ->
                val accountDocument = AccountDocument.of(
                    event.accountId,
                    event.amount ?: 0.0, // null이면 0.0으로 처리
                    event.eventDate
                )
                accountQueryMongoRepository.save(accountDocument)
                log.info("계좌 생성 이벤트 처리 완료: {}", event.accountId)
                null
            }
        } catch (e: Exception) {
            log.error("계좌 생성 이벤트 처리 실패: {}", event.accountId, e)
            throw EventHandlingException("계좌 생성 이벤트 처리 실패")
        }
    }

    /**
     * 입금 이벤트를 처리합니다.
     * MongoDB의 읽기 모델 잔액을 증가시킵니다.
     *
     * @param event 입금 이벤트
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleDeposit(event: MoneyDepositedEventEntity) {
        log.info("계좌 입금 이벤트 처리 시작: {}", event.accountId)
        updateBalanceWithRetry(event, true)
    }

    /**
     * 출금 이벤트를 처리합니다.
     * MongoDB의 읽기 모델 잔액을 감소시킵니다.
     *
     * @param event 출금 이벤트
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleWithdraw(event: MoneyWithdrawnEventEntity) {
        log.info("계좌 출금 이벤트 처리 시작: {}", event.accountId)
        updateBalanceWithRetry(event, false)
    }

    /**
     * 잔액 업데이트를 재시도 로직과 함께 수행합니다.
     *
     * @param event     계좌 이벤트
     * @param isDeposit true인 경우 입금, false인 경우 출금
     * @throws EventHandlingException 모든 재시도 후에도 처리 실패 시
     */
    private fun updateBalanceWithRetry(event: AbstractAccountEventEntity, isDeposit: Boolean) {
        try {
            retryTemplate.execute<Void, Exception> { _ ->
                updateBalance(event, isDeposit)
                log.info("잔액 업데이트 완료: {}", event.accountId)
                null
            }
        } catch (e: Exception) {
            log.error("잔액 업데이트 실패: {}", event.accountId, e)
            throw EventHandlingException("잔액 업데이트 실패")
        }
    }

    /**
     * MongoDB의 읽기 모델 잔액을 업데이트합니다.
     *
     * @param event     계좌 이벤트
     * @param isDeposit true인 경우 입금, false인 경우 출금
     * @throws IllegalArgumentException 계좌를 찾을 수 없는 경우
     */
    private fun updateBalance(event: AbstractAccountEventEntity, isDeposit: Boolean) {
        val accountDocument = accountQueryMongoRepository.findByAccountId(event.accountId)
            ?: throw IllegalArgumentException("MongoDB에서 계좌를 찾을 수 없습니다.")

        val newBalance = if (isDeposit)
            accountDocument.balance + (event.amount ?: 0.0)
        else
            accountDocument.balance - (event.amount ?: 0.0)

        accountDocument.changeBalance(newBalance)
        accountDocument.changeLastUpdated(event.eventDate)
        accountQueryMongoRepository.save(accountDocument)
    }

}