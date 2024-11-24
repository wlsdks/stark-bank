package com.example.cqrs.command.usecase;

import com.example.cqrs.command.entity.AccountWrite;
import com.example.cqrs.command.entity.event.AbstractAccountEvent;

import java.util.List;

public interface AccountWriteQueryUseCase {

    // 계좌 조회
    AccountWrite getAccount(String accountId);

    // 계좌 이력 조회
    List<AbstractAccountEvent> getAccountHistory(String accountId);

    // 사용자 거래 이력 조회
    List<AbstractAccountEvent> getUserTransactions(String userId);

    // 연관 거래 조회
    List<AbstractAccountEvent> getRelatedTransactions(String correlationId);

    // 활성 계좌 조회 메서드 추가
    List<String> getActiveAccountIds();

}