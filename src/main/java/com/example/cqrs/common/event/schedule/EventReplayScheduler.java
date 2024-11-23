package com.example.cqrs.common.event.schedule;

import com.example.cqrs.command.entity.event.AbstractAccountEvent;
import com.example.cqrs.command.entity.event.EventStatus;
import com.example.cqrs.command.usecase.AccountEventStoreUseCase;
import com.example.cqrs.common.service.EventReplayService;
import com.example.cqrs.query.usecase.AccountQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class EventReplayScheduler {

    private final EventReplayService eventReplayService;
    private final AccountQueryUseCase accountQueryUseCase;
    private final AccountEventStoreUseCase eventStoreUseCase;
    
    /**
     * @apiNote 매일 자정에 실행되는 스케줄러로, 모든 활성 계좌의 이벤트를 동기화합니다.
     */
    @Scheduled(cron = "0 0 0 * * *")  // 매일 자정
    public void syncDailyData() {
        log.info("Starting daily data synchronization...");
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);

        try {
            List<String> activeAccounts = accountQueryUseCase.getActiveAccountIds();
            log.info("Found {} active accounts to sync", activeAccounts.size());

            for (String accountId : activeAccounts) {
                try {
                    // 처리되지 않은 이벤트가 있는지 확인
                    List<AbstractAccountEvent> unprocessedEvents = findUnprocessedEvents(accountId, yesterday);

                    if (!unprocessedEvents.isEmpty()) {
                        log.info("Found {} unprocessed events for account: {}",
                                unprocessedEvents.size(), accountId);
                        eventReplayService.replayEvents(accountId, yesterday);
                        log.debug("Successfully synced account: {}", accountId);
                    } else {
                        log.debug("No unprocessed events for account: {}", accountId);
                    }
                } catch (Exception e) {
                    log.error("Failed to sync account: {}", accountId, e);
                }
            }

            log.info("Completed daily data synchronization");
        } catch (Exception e) {
            log.error("Failed to execute daily sync", e);
        }
    }


    /**
     * @param accountId 계좌 ID
     * @param fromDate  조회 시작 날짜
     * @return 처리되지 않은 이벤트 목록
     * @apiNote 지정된 날짜 이후에 처리되지 않은 이벤트를 조회합니다.
     */
    private List<AbstractAccountEvent> findUnprocessedEvents(String accountId, LocalDateTime fromDate) {
        return eventStoreUseCase.getEvents(accountId, fromDate).stream()
                .filter(event -> event.getStatus() != EventStatus.PROCESSED)
                .toList();
    }

}