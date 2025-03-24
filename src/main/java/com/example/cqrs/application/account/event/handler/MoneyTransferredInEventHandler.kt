package com.example.cqrs.application.account.event.handler

import com.example.cqrs.infrastructure.eventstore.entity.event.money.MoneyTransferredInEventEntity
import com.example.cqrs.infrastructure.persistence.query.repository.AccountMongoRepository
import org.slf4j.LoggerFactory
import org.springframework.retry.support.RetryTemplate
import org.springframework.stereotype.Component

@Component
class MoneyTransferredInEventHandler(
    private val accountMongoRepository: AccountMongoRepository,
    private val retryTemplate: RetryTemplate
) : EventHandler<MoneyTransferredInEventEntity> {

    private val log = LoggerFactory.getLogger(MoneyTransferredInEventHandler::class.java)

    override fun handle(event: MoneyTransferredInEventEntity) {
        log.info("이체 입금 이벤트 처리: {} <- {}", event.accountId, event.sourceAccountId)

        retryTemplate.execute<Void, Exception> { _ ->
            val accountDocument = accountMongoRepository.findByAccountId(event.accountId)
                ?: throw IllegalStateException("계좌를 찾을 수 없음: ${event.accountId}")

            val newBalance = accountDocument.balance + (event.amount ?: 0.0)
            accountDocument.changeBalance(newBalance)
            accountDocument.lastUpdated = event.eventDate
            accountMongoRepository.save(accountDocument)
            log.info("이체 입금 처리 완료: {}, 금액: {}, 새 잔액: {}", event.accountId, event.amount, newBalance)
            null
        }
    }

    override fun getEventType(): Class<MoneyTransferredInEventEntity> {
        return MoneyTransferredInEventEntity::class.java
    }

}