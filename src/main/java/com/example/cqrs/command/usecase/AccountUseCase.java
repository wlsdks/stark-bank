package com.example.cqrs.command.usecase;

import com.example.cqrs.command.entity.AccountEntity;
import com.example.cqrs.command.entity.event.AbstractAccountEventEntity;

import java.util.List;

public interface AccountUseCase {

    // 계좌 조회
    AccountEntity getAccount(String accountId);

    // 계좌 이력 조회
    List<AbstractAccountEventEntity> getAccountHistory(String accountId);

    // 사용자 거래 이력 조회
    List<AbstractAccountEventEntity> getUserTransactions(String userId);

    // 연관 거래 조회
    List<AbstractAccountEventEntity> getRelatedTransactions(String correlationId);

    // 활성 계좌 조회 메서드 추가
    List<String> getActiveAccountIds();

}
