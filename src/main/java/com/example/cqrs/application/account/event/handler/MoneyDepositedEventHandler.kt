package com.example.cqrs.application.account.event.handler

import com.example.cqrs.infrastructure.eventstore.entity.event.money.MoneyDepositedEventEntity
import com.example.cqrs.infrastructure.persistence.query.repository.AccountMongoRepository
import org.slf4j.LoggerFactory
import org.springframework.retry.support.RetryTemplate
import org.springframework.stereotype.Component

@Component
class MoneyDepositedEventHandler(
    private val accountMongoRepository: AccountMongoRepository,
    private val retryTemplate: RetryTemplate
) : EventHandler<MoneyDepositedEventEntity> {

    private val log = LoggerFactory.getLogger(MoneyDepositedEventHandler::class.java)

    override fun handle(event: MoneyDepositedEventEntity) {
        log.info("입금 이벤트 처리: {}", event.accountId)

        retryTemplate.execute<Void, Exception> { _ ->
            val accountDocument = accountMongoRepository.findByAccountId(event.accountId)
                ?: throw IllegalStateException("계좌를 찾을 수 없음: ${event.accountId}")

            val newBalance = accountDocument.balance + (event.amount ?: 0.0)
            accountDocument.changeBalance(newBalance)
            accountDocument.lastUpdated = event.eventDate
            accountMongoRepository.save(accountDocument)
            log.info("입금 처리 완료: {}, 금액: {}, 새 잔액: {}", event.accountId, event.amount, newBalance)
            null
        }
    }

    override fun getEventType(): Class<MoneyDepositedEventEntity> {
        return MoneyDepositedEventEntity::class.java
    }

}