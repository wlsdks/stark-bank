package com.example.cqrs.schedule;

import com.example.cqrs.service.AccountQueryService;
import com.example.cqrs.service.EventReplayService;
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
    private final AccountQueryService accountQueryService;

    @Scheduled(cron = "0 0 0 * * *")
    public void syncDailyData() {
        log.info("Starting daily data synchronization...");
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);

        try {
            List<String> activeAccounts = accountQueryService.getActiveAccountIds();
            log.info("Found {} active accounts to sync", activeAccounts.size());

            activeAccounts.forEach(accountId -> {
                try {
                    eventReplayService.replayEvents(accountId, yesterday);
                    log.debug("Successfully synced account: {}", accountId);
                } catch (Exception e) {
                    log.error("Failed to sync account: {}", accountId, e);
                }
            });

            log.info("Completed daily data synchronization");
        } catch (Exception e) {
            log.error("Failed to execute daily sync", e);
        }
    }

}