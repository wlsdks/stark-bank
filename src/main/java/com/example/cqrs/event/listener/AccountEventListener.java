package com.example.cqrs.event.listener;

import com.example.cqrs.entity.read.AccountView;
import com.example.cqrs.entity.write.event.AccountCreatedEvent;
import com.example.cqrs.entity.write.event.MoneyDepositedEvent;
import com.example.cqrs.entity.write.event.MoneyWithdrawnEvent;
import com.example.cqrs.entity.write.event.base.BaseAccountEvent;
import com.example.cqrs.exception.EventHandlingException;
import com.example.cqrs.repository.read.AccountViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 계좌 관련 이벤트를 처리하는 이벤트 핸들러입니다.
 * 이벤트 발생 시 읽기 모델(AccountView)을 업데이트하며,
 * 실패 시 재시도 메커니즘을 통해 안정성을 보장합니다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AccountEventListener {

    private final AccountViewRepository accountViewRepository;
    private final RetryTemplate retryTemplate;

    /**
     * 계좌 생성 이벤트를 처리합니다.
     * 새로운 읽기 모델 계정을 생성하며, 실패 시 최대 3번까지 재시도합니다.
     *
     * @param event 계좌 생성 이벤트
     * @throws EventHandlingException 모든 재시도 후에도 처리 실패 시
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(AccountCreatedEvent event) {
        log.info("Handling AccountCreatedEvent: {}", event.getAccountId());
        try {
            retryTemplate.execute(context -> {
                AccountView account = AccountView.of(
                        event.getAccountId(),
                        event.getAmount()
                );
                accountViewRepository.save(account);
                log.info("Successfully created read model for account: {}", event.getAccountId());
                return null;
            });
        } catch (Exception e) {
            log.error("Failed to handle AccountCreatedEvent after retries: {}", event.getAccountId(), e);
            throw new EventHandlingException("계좌 생성 이벤트 처리 실패", e);
        }
    }

    /**
     * 입금 이벤트를 처리합니다.
     * 계좌 잔액을 증가시키며, 실패 시 재시도합니다.
     *
     * @param event 입금 이벤트
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(MoneyDepositedEvent event) {
        log.info("Handling MoneyDepositedEvent for account: {}", event.getAccountId());
        updateBalanceWithRetry(event, true);
    }

    /**
     * 출금 이벤트를 처리합니다.
     * 계좌 잔액을 감소시키며, 실패 시 재시도합니다.
     *
     * @param event 출금 이벤트
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(MoneyWithdrawnEvent event) {
        log.info("Handling MoneyWithdrawnEvent for account: {}", event.getAccountId());
        updateBalanceWithRetry(event, false);
    }

    /**
     * 잔액 업데이트를 재시도 로직과 함께 수행합니다.
     *
     * @param event     계좌 이벤트
     * @param isDeposit true인 경우 입금, false인 경우 출금
     * @throws EventHandlingException 모든 재시도 후에도 처리 실패 시
     */
    private void updateBalanceWithRetry(BaseAccountEvent event, boolean isDeposit) {
        try {
            // execute 블록 안의 코드가 예외를 던지면 자동으로 재시도됩니다
            retryTemplate.execute(context -> {
                updateBalance(event, isDeposit); // 실패하면 재시도됨
                log.info("Successfully updated balance for account: {}", event.getAccountId());
                return null;
            });
        } catch (Exception e) {
            log.error("Failed to update balance after retries: {}", event.getAccountId(), e);
            throw new EventHandlingException("잔액 업데이트 실패", e);
        }
    }

    /**
     * 실제 잔액 업데이트를 수행합니다.
     *
     * @param event     계좌 이벤트
     * @param isDeposit true인 경우 입금, false인 경우 출금
     * @throws IllegalArgumentException 계좌를 찾을 수 없는 경우
     */
    private void updateBalance(BaseAccountEvent event, boolean isDeposit) {
        AccountView account = accountViewRepository.findById(event.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("계좌를 찾을 수 없습니다."));

        double newBalance = isDeposit ?
                account.getBalance() + event.getAmount() :
                account.getBalance() - event.getAmount();

        account.changeBalance(newBalance);
        accountViewRepository.save(account);
    }

}