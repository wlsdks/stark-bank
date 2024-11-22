package com.example.cqrs.service;

import com.example.cqrs.command.entity.event.AccountCreatedEvent;
import com.example.cqrs.command.entity.event.MoneyDepositedEvent;
import com.example.cqrs.command.entity.event.MoneyWithdrawnEvent;
import com.example.cqrs.command.entity.event.AbstractAccountEvent;
import com.example.cqrs.event.listener.AccountEventListener;
import com.example.cqrs.exception.EventReplayException;
import com.example.cqrs.command.service.useCase.AccountEventStoreUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 이벤트 재생을 담당하는 서비스 구현체입니다.
 * 읽기 모델(AccountView)을 재구성하거나 특정 시점부터 이벤트를 다시 적용할 때 사용됩니다.
 *
 * <p>주요 사용 사례:</p>
 * <ul>
 *     <li>읽기 모델과 이벤트 저장소 간의 불일치 발생 시 복구</li>
 *     <li>읽기 모델의 스키마가 변경되어 재구성이 필요한 경우</li>
 *     <li>특정 시점으로의 상태 복원이 필요한 경우</li>
 *     <li>데이터 검증이나 감사가 필요한 경우</li>
 * </ul>
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class EventReplayServiceImpl implements EventReplayService {

    private final AccountEventStoreUseCase accountEventStoreUseCase;  // 이벤트 저장소
    private final AccountEventListener eventHandler;     // 이벤트 핸들러
    private final RetryTemplate retryTemplate;          // 재시도 템플릿

    /**
     * 특정 계좌의 이벤트들을 주어진 날짜부터 재생합니다.
     * 각 이벤트는 재시도 메커니즘이 적용되어 처리되며, 모든 재시도 실패 시 예외가 발생합니다.
     *
     * @param accountId 재생할 계좌의 ID
     * @param fromDate  이벤트 재생을 시작할 날짜
     * @throws EventReplayException 이벤트 재생 중 오류 발생 시
     */
    @Transactional
    public void replayEvents(String accountId, LocalDateTime fromDate) {
        log.info("Starting event replay for account: {} from date: {}", accountId, fromDate);

        // 지정된 날짜 이후의 모든 이벤트 조회
        List<AbstractAccountEvent> events = accountEventStoreUseCase.getEvents(accountId, fromDate);

        // 각 이벤트를 재시도 로직과 함께 처리
        events.forEach(event -> {
            try {
                retryTemplate.execute(context -> {
                    processEvent(event);
                    return null;
                });
            } catch (Exception e) {
                log.error("Failed to replay event after retries. Event: {}", event, e);
                throw new EventReplayException("이벤트 재생 실패: " + event.getClass().getSimpleName(), e);
            }
        });

        log.info("Completed event replay for account: {}. Processed {} events",
                accountId, events.size());
    }

    /**
     * 개별 이벤트를 타입에 따라 적절한 핸들러에 전달하여 처리합니다.
     * Pattern matching for instanceof (Java 16+) 를 사용하여 타입 체크와 캐스팅을 동시에 수행합니다.
     *
     * @param event 처리할 이벤트
     */
    private void processEvent(AbstractAccountEvent event) {
        log.debug("Processing event: {}", event.getClass().getSimpleName());

        // 이벤트 타입에 따른 처리
        if (event instanceof AccountCreatedEvent createdEvent) {
            eventHandler.handle(createdEvent);         // 계좌 생성 이벤트 처리
        } else if (event instanceof MoneyDepositedEvent depositedEvent) {
            eventHandler.handle(depositedEvent);       // 입금 이벤트 처리
        } else if (event instanceof MoneyWithdrawnEvent withdrawnEvent) {
            eventHandler.handle(withdrawnEvent);       // 출금 이벤트 처리
        } else {
            log.warn("Unknown event type: {}", event.getClass().getName());
        }
    }

}