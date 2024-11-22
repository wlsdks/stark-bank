package com.example.cqrs.entity.read;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 읽기 전용 계좌 모델입니다.
 * CQRS의 읽기 모델 측면에서 올바르게 구현된 것입니다.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Entity
@Table(name = "account_read")
public class AccountView {

    @Id
    private String accountId; // 계좌 ID

    @Column(name = "balance")
    private double balance;   // 잔액

    // factory method
    public static AccountView of(String accountId, Double amount) {
        return new AccountView(accountId, amount);
    }

    // 잔액 변경 메서드
    public void changeBalance(double balance) {
        this.balance = balance;
    }

}
