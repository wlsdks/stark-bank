package com.example.cqrs.query.event.listener;

import com.example.cqrs.command.entity.event.*;
import com.example.cqrs.command.entity.event.AbstractAccountEventEntity;
import com.example.cqrs.command.entity.event.AccountCreatedEventEntity;
import com.example.cqrs.common.exception.EventHandlingException;
import com.example.cqrs.query.document.AccountDocument;
import com.example.cqrs.query.repository.AccountQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * MongoDB 읽기 모델을 업데이트하는 이벤트 핸들러입니다.
 * 계좌 관련 이벤트를 처리하여 MongoDB에 저장된 읽기 모델(AccountDocument)을 업데이트합니다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AccountEventListener {

    private final AccountQueryRepository accountQueryRepository;
    private final RetryTemplate retryTemplate;

    /**
     * 계좌 생성 이벤트를 처리합니다.
     * MongoDB에 새로운 읽기 모델 계정을 생성합니다.
     *
     * @param event 계좌 생성 이벤트
     * @throws EventHandlingException 모든 재시도 후에도 처리 실패 시
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAccountCreate(AccountCreatedEventEntity event) {
        log.info("계좌 생성 이벤트 처리 시작: {}", event.getAccountId());
        try {
            retryTemplate.execute(context -> {
                AccountDocument accountDocument = AccountDocument.of(
                        event.getAccountId(),
                        event.getAmount(),
                        event.getEventDate()
                );
                accountQueryRepository.save(accountDocument);
                log.info("계좌 생성 이벤트 처리 완료: {}", event.getAccountId());
                return null;
            });
        } catch (Exception e) {
            log.error("계좌 생성 이벤트 처리 실패: {}", event.getAccountId(), e);
            throw new EventHandlingException("계좌 생성 이벤트 처리 실패", e);
        }
    }

    /**
     * 입금 이벤트를 처리합니다.
     * MongoDB의 읽기 모델 잔액을 증가시킵니다.
     *
     * @param event 입금 이벤트
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDeposit(MoneyDepositedEventEntity event) {
        log.info("계좌 입금 이벤트 처리 시작: {}", event.getAccountId());
        updateBalanceWithRetry(event, true);
    }

    /**
     * 출금 이벤트를 처리합니다.
     * MongoDB의 읽기 모델 잔액을 감소시킵니다.
     *
     * @param event 출금 이벤트
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleWithdraw(MoneyWithdrawnEventEntity event) {
        log.info("계좌 출금 이벤트 처리 시작: {}", event.getAccountId());
        updateBalanceWithRetry(event, false);
    }

    /**
     * 잔액 업데이트를 재시도 로직과 함께 수행합니다.
     *
     * @param event     계좌 이벤트
     * @param isDeposit true인 경우 입금, false인 경우 출금
     * @throws EventHandlingException 모든 재시도 후에도 처리 실패 시
     */
    private void updateBalanceWithRetry(AbstractAccountEventEntity event, boolean isDeposit) {
        try {
            retryTemplate.execute(context -> {
                updateBalance(event, isDeposit);
                log.info("잔액 업데이트 완료: {}", event.getAccountId());
                return null;
            });
        } catch (Exception e) {
            log.error("잔액 업데이트 실패: {}", event.getAccountId(), e);
            throw new EventHandlingException("잔액 업데이트 실패", e);
        }
    }

    /**
     * MongoDB의 읽기 모델 잔액을 업데이트합니다.
     *
     * @param event     계좌 이벤트
     * @param isDeposit true인 경우 입금, false인 경우 출금
     * @throws IllegalArgumentException 계좌를 찾을 수 없는 경우
     */
    private void updateBalance(AbstractAccountEventEntity event, boolean isDeposit) {
        AccountDocument accountDocument = accountQueryRepository.findByAccountId(event.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("MongoDB에서 계좌를 찾을 수 없습니다."));

        double newBalance = isDeposit
                ? accountDocument.getBalance() + event.getAmount()
                : accountDocument.getBalance() - event.getAmount();

        accountDocument.changeBalance(newBalance);
        accountDocument.changeLastUpdated(event.getEventDate());
        accountQueryRepository.save(accountDocument);
    }
}
