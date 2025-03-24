package com.example.cqrs.application.account.event.handler

import com.example.cqrs.infrastructure.eventstore.entity.event.account.AccountCreatedEventEntity
import com.example.cqrs.infrastructure.persistence.query.document.AccountDocument
import com.example.cqrs.infrastructure.persistence.query.repository.AccountMongoRepository
import org.slf4j.LoggerFactory
import org.springframework.retry.support.RetryTemplate
import org.springframework.stereotype.Component

@Component
class AccountCreatedEventHandler(
    private val accountMongoRepository: AccountMongoRepository,
    private val retryTemplate: RetryTemplate
) : EventHandler<AccountCreatedEventEntity> {

    private val log = LoggerFactory.getLogger(AccountCreatedEventHandler::class.java)

    override fun handle(event: AccountCreatedEventEntity) {
        log.info("계좌 생성 이벤트를 처리합니다. : ${event.accountId}")

        retryTemplate.execute<Void, Exception> { _ ->
            val accountDocument = AccountDocument.of(
                accountId = event.accountId,
                balance = event.amount ?: 0.0,
                lastUpdated = event.eventDate
            )
            accountMongoRepository.save(accountDocument)
            log.info("계좌 생성 이벤트 처리 완료 : ${event.accountId}")
            null
        }
    }

    override fun getEventType(): Class<AccountCreatedEventEntity> {
        return AccountCreatedEventEntity::class.java
    }

}