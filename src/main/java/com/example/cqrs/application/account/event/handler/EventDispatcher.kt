package com.example.cqrs.application.account.event.handler

import com.example.cqrs.infrastructure.eventstore.entity.base.EventEntity
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Service
class EventDispatcher(
    // EventDispatcher가 생성될 때, Spring은 EventHandler<*> 인터페이스를 구현한 모든 빈을 찾아서 eventHandlers 리스트에 주입합니다.
    private val eventHandlers: List<EventHandler<*>>
) {

    private val log = LoggerFactory.getLogger(EventDispatcher::class.java)
    private val handlerMap = mutableMapOf<Class<out EventEntity>, EventHandler<EventEntity>>()

    // 핸들러 앱 초기화
    init {
        @Suppress("UNCHECKED_CAST")
        eventHandlers.forEach { handler ->
            // 자신이 처리할 수 있는 이벤트 타입(클래스)를 반환합니다.
            val eventType = handler.getEventType()

            // 이벤트 타입을 키로, 핸들러를 값으로 하는 맵에 저장합니다.
            handlerMap[eventType] = handler as EventHandler<EventEntity>

            log.info("등록된 이벤트 핸들러: ${handler.javaClass.simpleName} -> ${eventType.simpleName}")
        }
    }

    /**
     * 이벤트를 받아 적절한 핸들러로 디스패치합니다.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun dispatch(event: EventEntity) {
        val handler = handlerMap[event.javaClass]
        if (handler != null) {
            log.debug("이벤트 처리 중입니다. : ${event.javaClass.simpleName}")
            try {
                handler.handle(event)
            } catch (e: Exception) {
                event.markAsFailed()
            }
        } else {
            log.warn("처리할 수 있는 핸들러가 없습니다. : ${event.javaClass.simpleName}")
        }
    }

}