package com.example.cqrs.command.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 쓰기 전용 모델인 계좌 엔티티입니다.
 * CQRS의 읽기 모델 측면에서 올바르게 구현된 것입니다.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Entity
@Table(name = "account")
public class AccountEntity {

    @Id
    private String accountId; // 계좌 ID

    @Column(name = "balance")
    private double balance;   // 잔액

    // factory method
    public static AccountEntity of(String accountId, Double amount) {
        return new AccountEntity(accountId, amount);
    }

    // 잔액 변경 메서드
    public void changeBalance(double balance) {
        this.balance = balance;
    }

    // 출금 잔액 검증 메서드
    public void checkAvailableWithdraw(double amount) {
        if (this.balance < amount) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }
    }

}
