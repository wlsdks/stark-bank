package com.example.cqrs.application.account.event.handler

import com.example.cqrs.infrastructure.eventstore.entity.base.EventEntity
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

/**
 * MongoDB 읽기 모델을 업데이트하는 이벤트 핸들러입니다.
 * 계좌 관련 이벤트를 처리하여 MongoDB에 저장된 읽기 모델(AccountDocument)을 업데이트합니다.
 */
@Service
class AccountEventListener(
    private val eventDispatcher: EventDispatcher
) {
    private val log = LoggerFactory.getLogger(AccountEventListener::class.java)

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleEvent(event: EventEntity) {
        // 모든 이벤트 타입을 디스패처로 전달
        eventDispatcher.dispatch(event)
    }

}