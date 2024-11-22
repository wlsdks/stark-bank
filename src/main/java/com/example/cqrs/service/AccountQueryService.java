package com.example.cqrs.service;

import com.example.cqrs.entity.read.AccountView;
import com.example.cqrs.entity.write.event.base.AbstractAccountEvent;

import java.util.List;

public interface AccountQueryService {

    // 계좌 조회
    AccountView getAccount(String accountId);

    // 계좌 이력 조회
    List<AbstractAccountEvent> getAccountHistory(String accountId);

    // 사용자 거래 이력 조회
    List<AbstractAccountEvent> getUserTransactions(String userId);

    // 연관 거래 조회
    List<AbstractAccountEvent> getRelatedTransactions(String correlationId);

    // 활성 계좌 조회 메서드 추가
    List<String> getActiveAccountIds();

}
