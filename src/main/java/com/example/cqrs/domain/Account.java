package com.example.cqrs.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 계좌의 현재 상태를 나타내는 도메인 모델입니다.
 * 불변 객체로 설계되어 있으며, 상태 변경 시 새로운 객체를 생성합니다.
 * 이벤트 소싱에서 애그리게이트(Aggregate)로서의 역할을 수행합니다.
 */
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Getter
public class Account {

    private String accountId; // 계좌 ID
    private double balance;   // 잔액

    // factory method
    public static Account of(String accountId, double balance) {
        return new Account(accountId, balance);
    }

    // 출금 잔액 검증 메서드
    public void checkAvailableWithdraw(double amount) {
        if (this.balance < amount) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }
    }

}
