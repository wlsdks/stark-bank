package com.example.cqrs.application.account.event.handler

import com.example.cqrs.infrastructure.eventstore.entity.event.money.BalanceChangeFailedEventEntity
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class BalanceChangeFailedEventHandler : EventHandler<BalanceChangeFailedEventEntity> {

    private val log = LoggerFactory.getLogger(BalanceChangeFailedEventHandler::class.java)

    override fun handle(event: BalanceChangeFailedEventEntity) {
        log.warn(
            "잔액 변경 실패: {}, 사유: {}, 작업 타입: {}",
            event.accountId, event.reason, event.operationType
        )

        // 실패 로그 저장이나 알림 로직 구현하기
    }

    override fun getEventType(): Class<BalanceChangeFailedEventEntity> {
        return BalanceChangeFailedEventEntity::class.java
    }

}