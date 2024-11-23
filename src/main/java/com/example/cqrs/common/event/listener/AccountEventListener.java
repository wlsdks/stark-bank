package com.example.cqrs.common.event.listener;

import com.example.cqrs.command.entity.event.AbstractAccountEvent;
import com.example.cqrs.command.entity.event.AccountCreatedEvent;
import com.example.cqrs.command.entity.event.MoneyDepositedEvent;
import com.example.cqrs.command.entity.event.MoneyWithdrawnEvent;
import com.example.cqrs.command.usecase.AccountEventStoreUseCase;
import com.example.cqrs.common.exception.EventHandlingException;
import com.example.cqrs.query.entity.AccountView;
import com.example.cqrs.query.repository.AccountViewRepository;
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
    private final AccountEventStoreUseCase accountEventStoreUseCase;
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
    public void handleAccountCreate(AccountCreatedEvent event) {
        log.info("계좌 생성 이벤트 시작: {}", event.getAccountId());
        try {
            retryTemplate.execute(context -> {
                AccountView account = AccountView.of(
                        event.getAccountId(),
                        event.getAmount()
                );
                accountViewRepository.save(account);
                event.markAsProcessed();  // 이벤트 처리 완료 표시
                accountEventStoreUseCase.saveEventStatus(event);  // 상태 업데이트
                log.info("계좌 생성 이벤트 처리 완료: {}", event.getAccountId());
                return null;
            });
        } catch (Exception e) {
            log.error("계좌 생성 이벤트 처리 실패: {}", event.getAccountId(), e);
            event.markAsFailed();  // 이벤트 처리 실패 표시
            accountEventStoreUseCase.saveEventStatus(event);  // 실패 상태 업데이트
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
    public void handleDeposit(MoneyDepositedEvent event) {
        log.info("계좌 입금 이벤트 처리: {}", event.getAccountId());
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
    public void handleWithdraw(MoneyWithdrawnEvent event) {
        log.info("계좌 출금 이벤트 처리: {}", event.getAccountId());
        updateBalanceWithRetry(event, false);
    }

    /**
     * 잔액 업데이트를 재시도 로직과 함께 수행합니다.
     *
     * @param event     계좌 이벤트
     * @param isDeposit true인 경우 입금, false인 경우 출금
     * @throws EventHandlingException 모든 재시도 후에도 처리 실패 시
     */
    private void updateBalanceWithRetry(AbstractAccountEvent event, boolean isDeposit) {
        try {
            retryTemplate.execute(context -> {
                updateBalance(event, isDeposit);
                event.markAsProcessed();  // 이벤트 처리 완료 표시
                accountEventStoreUseCase.saveEventStatus(event);  // 상태 업데이트
                log.info("잔액 업데이트 완료: {}", event.getAccountId());
                return null;
            });
        } catch (Exception e) {
            log.error("잔액 업데이트 실패: {}", event.getAccountId(), e);
            event.markAsFailed();  // 이벤트 처리 실패 표시
            accountEventStoreUseCase.saveEventStatus(event);  // 실패 상태 업데이트
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
    private void updateBalance(AbstractAccountEvent event, boolean isDeposit) {
        AccountView account = accountViewRepository.findById(event.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("계좌를 찾을 수 없습니다."));

        double newBalance = isDeposit ?
                account.getBalance() + event.getAmount() :
                account.getBalance() - event.getAmount();

        account.changeBalance(newBalance);
        accountViewRepository.save(account);
    }

}